package secondlife.network.hcfactions.utilties;

import lombok.Getter;

@Getter
public class ChunkPosition {

    private byte x;
    private byte z;

    public ChunkPosition(byte x, byte z) {
        this.x = x;
        this.z = z;
    }
}

