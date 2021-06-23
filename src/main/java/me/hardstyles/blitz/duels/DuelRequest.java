package me.hardstyles.blitz.duels;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class DuelRequest {
    private UUID sender;
    private UUID target;
}
