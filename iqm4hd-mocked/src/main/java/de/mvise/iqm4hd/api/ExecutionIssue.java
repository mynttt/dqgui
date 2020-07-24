package de.mvise.iqm4hd.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExecutionIssue {
    private int type;
    private String severity;
    private double score;
    private Object[] identifier;
    private Object[] value;
    private Map<String, Object[]> info = new HashMap<>();

    public ExecutionIssue(int type, double score) {
        this.type = type;
        this.score = score;
    }

    public void addInfo(String key, Object[] value) {
        info.put(key, value);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Object[] getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Object[] identifier) {
        this.identifier = identifier;
    }

    public Object[] getValue() {
        return value;
    }

    public void setValue(Object[] value) {
        this.value = value;
    }

    public Map<String, Object[]> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object[]> info) {
        this.info = info;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(identifier);
        result = prime * result + Arrays.deepHashCode(value);
        result = prime * result + Objects.hash(info, score, severity, type);
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
        ExecutionIssue other = (ExecutionIssue) obj;
        return Arrays.deepEquals(identifier, other.identifier) && Objects.equals(info, other.info)
                && Double.doubleToLongBits(score) == Double.doubleToLongBits(other.score)
                && Objects.equals(severity, other.severity) && type == other.type
                && Arrays.deepEquals(value, other.value);
    }
}