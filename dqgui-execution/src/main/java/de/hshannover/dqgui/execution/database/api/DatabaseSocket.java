package de.hshannover.dqgui.execution.database.api;

import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * Store a socket (host + port).
 * The HostFormat Enum exists in case the format is important for the database engine.
 *
 * @author Marc Herschel
 *
 */
public final class DatabaseSocket {

    /**
     * Possible host formats
     */
    public enum HostFormat {
        IPv4, IPv6, DOMAIN
    }

    private final HostFormat format;
    private final String host;
    private final int port;

    /**
     * Construct a new socket.
     * @param host to use.
     * @param port to use.
     * @throws IllegalArgumentException if host is null.
     */
    public DatabaseSocket(String host, int port) {
        if(host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Host must not be null/empty.");
        }
        this.host = host;
        this.port = port;
        if(InetAddressValidator.getInstance().isValidInet4Address(host)) {
            this.format = HostFormat.IPv4;
            return;
        }
        if(InetAddressValidator.getInstance().isValidInet6Address(host)) {
            this.format = HostFormat.IPv6;
            return;
        }
        this.format = HostFormat.DOMAIN;
    }

    public HostFormat getFormat() {
        return format;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String asAddress() {
        return host + ":" + port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatabaseSocket other = (DatabaseSocket) obj;
        if (format != other.format)
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DatabaseSocket [format=" + format + ", host=" + host + ", port=" + port + "]";
    }
}
