package com.epam.nosql.model;

import java.util.Objects;

public final class Response {
    private final boolean success;
    private final String message;
    private final String id;
    private final String index;

    public Response(boolean success,
                    String message,
                    String id,
                    String index) {
        this.success = success;
        this.message = message;
        this.id = id;
        this.index = index;
    }

    public boolean success() {
        return success;
    }

    public String message() {
        return message;
    }

    public String id() {
        return id;
    }

    public String index() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Response) obj;
        return this.success == that.success &&
                Objects.equals(this.message, that.message) &&
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, id, index);
    }

    @Override
    public String toString() {
        return "ElasticsearchResponse[" +
                "success=" + success + ", " +
                "message=" + message + ", " +
                "id=" + id + ", " +
                "index=" + index + ']';
    }

}
