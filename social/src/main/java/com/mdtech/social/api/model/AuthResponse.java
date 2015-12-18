package com.mdtech.social.api.model;

public class AuthResponse extends AbstractResponse {

    private User user;

    public static class LoginRequest {
        public String username;
        public String password;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class SignupCheckRes {
        public boolean isValid;
        public String value;
        public boolean isValid() {
            return isValid;
        }
        public void setValid(boolean isValid) {
            this.isValid = isValid;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
