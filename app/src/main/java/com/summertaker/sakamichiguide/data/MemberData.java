package com.summertaker.sakamichiguide.data;

import java.io.Serializable;

public class MemberData implements Serializable, Comparable<MemberData> {

    private static final long serialVersionUID = 1L;

    String id;
    String groupId;
    String groupName;
    String teamName;
    String generation;

    String name;
    String nameKo;
    String nameEn;
    String nameJa;
    String furigana;
    String nameId;
    String nameCn;
    String pinyin;
    String localName;
    String localeName;
    String noSpaceName;

    String nickname;
    String nicknameJa;
    String nicknameId;
    String nicknameCn;
    String nicknameEn;
    String nicknameKo;

    String info;
    String birth;
    String birthday;
    String hometown;
    String debut;
    String debutDay;

    String thumbnailUrl;
    String imageUrl;
    String profileUrl;
    String mobileUrl;

    String blogName;
    String blogUrl;
    String namuwikiUrl;
    String namuwikiInfo;
    String pedia48Url;
    String pedia48ThumbnailUrl;
    String pedia48ImageUrl;
    String stage48Url;

    String googlePlusId;
    String googlePlusUrl;
    String instagramId;
    String instagramUrl;
    String twitterId;
    String twitterUrl;
    String facebookId;
    String facebookUrl;
    String nanagogoUrl;
    String weiboUrl;
    String qqUrl;
    String baiduUrl;

    String role;
    boolean isGeneralManager;
    boolean isManager;
    boolean isGeneralCaptain;
    boolean isCaptain;
    boolean isViceCaptain;
    boolean isConcurrentPosition;

    public int compareTo(MemberData other) {
        return name.compareTo(other.name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameJa() {
        return nameJa;
    }

    public void setNameJa(String nameJa) {
        this.nameJa = nameJa;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String nameId) {
        this.nameId = nameId;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getNameKo() {
        return nameKo;
    }

    public void setNameKo(String nameKo) {
        this.nameKo = nameKo;
    }

    public String getFurigana() {
        return furigana;
    }

    public void setFurigana(String furigana) {
        this.furigana = furigana;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNicknameJa() {
        return nicknameJa;
    }

    public void setNicknameJa(String nicknameJa) {
        this.nicknameJa = nicknameJa;
    }

    public String getNicknameId() {
        return nicknameId;
    }

    public void setNicknameId(String nicknameId) {
        this.nicknameId = nicknameId;
    }

    public String getNicknameCn() {
        return nicknameCn;
    }

    public void setNicknameCn(String nicknameCn) {
        this.nicknameCn = nicknameCn;
    }

    public String getNicknameEn() {
        return nicknameEn;
    }

    public void setNicknameEn(String nicknameEn) {
        this.nicknameEn = nicknameEn;
    }

    public String getNicknameKo() {
        return nicknameKo;
    }

    public void setNicknameKo(String nicknameKo) {
        this.nicknameKo = nicknameKo;
    }

    public String getNoSpaceName() {
        return noSpaceName;
    }

    public void setNoSpaceName(String noSpaceName) {
        this.noSpaceName = noSpaceName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getDebutDay() {
        return debutDay;
    }

    public void setDebutDay(String debutDay) {
        this.debutDay = debutDay;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getPedia48Url() {
        return pedia48Url;
    }

    public void setPedia48Url(String pedia48Url) {
        this.pedia48Url = pedia48Url;
    }

    public String getPedia48ThumbnailUrl() {
        return pedia48ThumbnailUrl;
    }

    public void setPedia48ThumbnailUrl(String pedia48ThumbnailUrl) {
        this.pedia48ThumbnailUrl = pedia48ThumbnailUrl;
    }

    public String getPedia48ImageUrl() {
        return pedia48ImageUrl;
    }

    public void setPedia48ImageUrl(String pedia48ImageUrl) {
        this.pedia48ImageUrl = pedia48ImageUrl;
    }

    public String getNamuwikiUrl() {
        return namuwikiUrl;
    }

    public void setNamuwikiUrl(String namuwikiUrl) {
        this.namuwikiUrl = namuwikiUrl;
    }

    public String getNamuwikiInfo() {
        return namuwikiInfo;
    }

    public void setNamuwikiInfo(String namuwikiInfo) {
        this.namuwikiInfo = namuwikiInfo;
    }

    public String getStage48Url() {
        return stage48Url;
    }

    public void setStage48Url(String stage48Url) {
        this.stage48Url = stage48Url;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }

    public String getGooglePlusUrl() {
        return googlePlusUrl;
    }

    public void setGooglePlusUrl(String googlePlusUrl) {
        this.googlePlusUrl = googlePlusUrl;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public void setInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getNanagogoUrl() {
        return nanagogoUrl;
    }

    public void setNanagogoUrl(String nanagogoUrl) {
        this.nanagogoUrl = nanagogoUrl;
    }

    public String getWeiboUrl() {
        return weiboUrl;
    }

    public void setWeiboUrl(String weiboUrl) {
        this.weiboUrl = weiboUrl;
    }

    public String getQqUrl() {
        return qqUrl;
    }

    public void setQqUrl(String qqUrl) {
        this.qqUrl = qqUrl;
    }

    public String getBaiduUrl() {
        return baiduUrl;
    }

    public void setBaiduUrl(String baiduUrl) {
        this.baiduUrl = baiduUrl;
    }

    public boolean isCaptain() {
        return isCaptain;
    }

    public void setCaptain(boolean captain) {
        isCaptain = captain;
    }

    public boolean isViceCaptain() {
        return isViceCaptain;
    }

    public void setViceCaptain(boolean viceCaptain) {
        isViceCaptain = viceCaptain;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isConcurrentPosition() {
        return isConcurrentPosition;
    }

    public void setConcurrentPosition(boolean concurrentPosition) {
        isConcurrentPosition = concurrentPosition;
    }

    public boolean isGeneralCaptain() {
        return isGeneralCaptain;
    }

    public void setGeneralCaptain(boolean generalCaptain) {
        isGeneralCaptain = generalCaptain;
    }

    public boolean isGeneralManager() {
        return isGeneralManager;
    }

    public void setGeneralManager(boolean generalManager) {
        isGeneralManager = generalManager;
    }
}
