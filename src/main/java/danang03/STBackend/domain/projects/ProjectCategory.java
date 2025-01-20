package danang03.STBackend.domain.projects;

public enum ProjectCategory {
    WEB_APPLICATION("Web Application"),
    MOBILE_APPLICATION("Mobile Application"),
    DESKTOP_APPLICATION("Desktop Application"),
    GAME_DEVELOPMENT("Game Development"),
    AI_MACHINE_LEARNING("AI/Machine Learning"),
    BLOCKCHAIN_DAPP("Blockchain/Dapp"),
    OPEN_SOURCE("Open Source Project"),
    AUTOMATION_SCRIPT("Automation/Script"),
    DATABASE_BACKEND("Database/Backend");

    private final String displayName;

    ProjectCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
