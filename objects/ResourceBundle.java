package objects;

public class ResourceBundle {
    private FirmTypeA firmTypeA;
    private int[] otherResIndex;
    private int[] correspondingSelfResIndex;

    public ResourceBundle() {
    }

    public ResourceBundle(FirmTypeA firmTypeA, int[] otherResIndex, int[] correspondingSelfResIndex) {
        this.firmTypeA = firmTypeA;
        this.otherResIndex = otherResIndex;
        this.correspondingSelfResIndex = correspondingSelfResIndex;
    }

    public FirmTypeA getFirmTypeA() {
        return firmTypeA;
    }

    public void setFirmTypeA(FirmTypeA firmTypeA) {
        this.firmTypeA = firmTypeA;
    }

    public int[] getOtherResIndex() {
        return otherResIndex;
    }

    public void setOtherResIndex(int[] otherResIndex) {
        this.otherResIndex = otherResIndex;
    }

    public int[] getCorrespondingSelfResIndex() {
        return correspondingSelfResIndex;
    }

    public void setCorrespondingSelfResIndex(int[] correspondingSelfResIndex) {
        this.correspondingSelfResIndex = correspondingSelfResIndex;
    }
}
