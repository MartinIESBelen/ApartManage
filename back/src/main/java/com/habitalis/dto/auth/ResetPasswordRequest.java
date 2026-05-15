package com.habitalis.dto.auth;

public record ResetPasswordRequest (String token, String nuevaPassword){
}
