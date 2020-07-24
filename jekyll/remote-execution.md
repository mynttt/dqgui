---
layout: article
title: "Remote Execution Server"
category: doc
---

### Configuration

The remote execution server is configured by the `config.json` file. Upon starting the server the first time it will generate a default configuration, store it in PWD and shutdown with a message indicating that you should review the configuration file before starting it again.

Starting the server from now on will always load the configuration.

```json
{
  "enforceSSL": false,
  "rServe": false,
  "port": 9999,
  "executorPoolSize": 8,
  "pruneUncollectedEveryHours": 24, 
  "keepJobsForDays": 30,
  "key": "f369bd256006c86616971f9b7caf14da487dd286bbec096f5d28e9731487f4dc"
}
```

- <span style="color: red">`enforceSSL`</span>: Will redirect http traffic to https
- <span style="color: red">`rServe`</span>: Will connect to a rServe instance or throw an exception if a connection fails. The connection is done through the `de.mvise.iqm4hd.client.RConn` singleton.
- <span style="color: red">`port`</span>: Allows to specify the port of the server
- <span style="color: red">`executorPoolSize`</span>: How large should the IQM4HD worker pool be?
- <span style="color: red">`pruneUncollectedEveryHours`</span>: How often in hours should the prune housekeeping task run?
- <span style="color: red">`keepJobsForDays`</span>: How long should uncollected jobs be held by the server?
- <span style="color: red">`key`</span>: generated on config creation, is used to restrict access to the server

A folder named jobs will also be created to store finished uncollected jobs so a server restart / crash won't cause a data loss.

### Internal Workings

Sending a job to the server will enqueue it with an executor service, upon completion, the job will be transferred from memory into the `jobs` folder.

Clients connected via the event websocket are notified about completion events and can then collect the job.

Collection will remove the job from the server and send transfer the results to the client, it is then the clients duty to propagate the results by either uploading it to the database or storing it in a filesystem project.

Every DQGUI project has a UUID assigned to it so the client will know what to do with the job.

The server must be able to connect to the targeted database. If a client connects to a database through an SSH tunnel, the remote server must also have that SSH tunnel open. It probably makes sense to host the server on the same machine as the database and then connect through an SSH tunnel to the server.

A good practice would be to use SSH tunnels to the database on the server and client side. As long as the port is the same the server will be able to connect through the tunnel.

**Please only connect to the remote execution server via an SSH tunnel or HTTPS as the database credentials will be sent via a JSON payload!**

### Endpoints

Every request needs to have the header `X-Key` set to the matching key. 

These endpoints do not follow the standardized REST verbs and specifications.

Success is signaled by a 200 response, 401 signals an invalid key.

All endpoints will send a [RemoteError](../javaDocs/de/hshannover/dqgui/execution/model/remote/Remote.html) object with status code 500 if an error occurs.

| Type  | Endpoint  | Model In| Model Out | Description |
| ------------- |:-------------:|:-------------:|:-------------:|:-------------:|
|`GET`| `/execution`| none | [RemoteStatusReports](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteStatusReports.html)|List all jobs|
|`POST`|`/execution`| [RemoteJob](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteJob.html) | [RemoteStatusReport](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteStatusReport.html) | Submit a job|
|`POST`|`/execution/progress`|[RemoteProgressRequest](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteProgressRequest.html)|[RemoteProgressResponse](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteProgressResponse.html)| Request log of running job|
|`GET`|`/execution/test`|None|None|Useless test endpoint|
|`POST`|`/execution/collect`|[RemoteCollectionRequest](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteCollectionRequest.html)|[RemoteCollectionResponse](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteCollectionResponse.html)|Collect finished job results|

<p></p>

An event websocket running at `/events` can be subscribed to if you want to receive completion events, the model for them is [RemoteSocketNotification](../javaDocs/de/hshannover/dqgui/execution/model/remote/RemoteSocketNotification.html).