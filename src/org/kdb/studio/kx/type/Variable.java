package org.kdb.studio.kx.type;

public class Variable extends KBase {
    public String getDataType() {
        return "Variable";
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    private String name;
    private String context;
}