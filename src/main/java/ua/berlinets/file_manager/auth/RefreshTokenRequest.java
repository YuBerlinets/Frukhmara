package ua.berlinets.file_manager.auth;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
