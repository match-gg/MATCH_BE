package gg.match.domain.board.valorant.entity

import java.util.*

enum class ValorantCharacters(
    val id: String,
    val koName: String
) {
    GEKKO("E370FA57-4757-3604-3648-499E1F642D3F", "게코"),
    FADE("DADE69B4-4F5A-8528-247B-219E5A1FACD6", "페이드"),
    BREACH("5F8D3A7F-467B-97F3-062C-13ACF203C006", "브리치"),
    DEADLOCK("CC8B64C8-4B25-4FF9-6E7F-37B4DA43D235", "데드록"),
    RAZE("F94C3B30-42BE-E959-889C-5AA313DBA261", "레이즈"),
    CHAMBER("22697A3D-45BF-8DD7-4FEC-84A9E28C69D7", "체임버"),
    KAY_O("601DBBE7-43CE-BE57-2A40-4ABD24953621", "케이/오"),
    SKYE("6F2A04CA-43E0-BE17-7F36-B3908627744D", "스카이"),
    CYPHER("117ED9E3-49F3-6512-3CCF-0CADA7E3823B", "사이퍼"),
    SOVA("320B2A48-4D9B-A075-30F1-1F93A9B638FA", "소바"),
    KILLJOY("1E58DE9C-4950-5125-93E9-A0AEE9F98746", "킬조이"),
    HARBOR("95B78ED7-4637-86D9-7E41-71BA8C293152", "하버"),
    VIPER("707EAB51-4836-F488-046A-CDA6BF494859", "바이퍼"),
    PHOENIX("EB93336A-449B-9C1B-0A54-A891F7921D69", "피닉스"),
    ASTRA("41FB69C1-4189-7B37-F117-BCAF1E96F1BF", "아스트라"),
    BRIMSTONE("9F0D8BA9-4140-B941-57D3-A7AD57C6B417", "브림스톤"),
    NEON("BB2A4828-46EB-8CD1-E765-15848195D751", "네온"),
    YORU("7F94D92C-4234-0A36-9646-3A87EB8B5C89", "요루"),
    SAGE("569FDD95-4D10-43AB-CA70-79BECC718B46", "세이지"),
    REYNA("A3BFB853-43B2-7238-A4F1-AD90E9E46BCC", "레이나"),
    OMEN("8E253930-4C05-31DD-1B6C-968525494517", "오멘"),
    JETT("ADD6443A-41BD-E414-F6AD-E58D267F4E95", "제트"),
    NO_DATA("36FB82AF-409D-C0ED-4B49-57B1EB08FBD5", "No_Data")
    ;

    companion object{
        fun characterIdToName(id: String): ValorantCharacters? {
            return Arrays.stream(ValorantCharacters.values())
                .filter{ v -> v.id == id }
                .findAny()
                .orElse(null)
        }
    }
}