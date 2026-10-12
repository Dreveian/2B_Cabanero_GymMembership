package com.scc.model;

public record User(int user_id, String username, String password, String fullname, String role, String created_at) {
    @Override
    public String toString(){
        return String.format("[%d] %s (%s) - Status: %s", user_id, username, fullname, role, created_at );

    }
}
