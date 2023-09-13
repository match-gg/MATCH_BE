package gg.match.domain.board.valorant.entity

import java.util.*

enum class ValorantGameModes(
    val id: String,
    val assetPath: String,
    val koName: String
) {
    STANDARD("96BD3920-4F36-D026-2B28-C683EB0BCAC5", "/Game/GameModes/Bomb/BombGameMode.BombGameMode_C", "기본"),
    ESCALATION("A4ED6518-4741-6DCB-35BD-F884AECDC859", "/Game/GameModes/GunGame/GunGameTeamsGameMode.GunGameTeamsGameMode_C", "에스컬레이션"),
    TEAM_DEATHMATCH("E086DB66-47FD-E791-CA81-06A645AC7661", "/Game/GameModes/HURM/HURMGameMode.HURMGameMode_C", "팀 데스매치"),
    SPIKE_RUSH("E921D1E6-416B-C31F-1291-74930C330B7B", "/Game/GameModes/QuickBomb/QuickBombGameMode.QuickBombGameMode_C", "스파이크 돌격"),
    SWIFTPLAY("5D0F264B-4EBE-CC63-C147-809E1374484B", "/Game/GameModes/_Development/Swiftplay_EndOfRoundCredits/Swiftplay_EoRCredits_GameMode.Swiftplay_EoRCredits_GameMode_C", "신속플레이")
    ;

    companion object{
        fun assetPathToName(assetPath: String): ValorantGameModes? {
            return Arrays.stream(values())
                .filter{ v -> v.assetPath == assetPath }
                .findAny()
                .orElse(null)
        }
    }
}