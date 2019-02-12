package secondlife.network.paik.client;

import lombok.Getter;

@Getter
public class ModClientType implements ClientType {
    
    private String name;
    private String modId;
    private String modVersion;
    
    @Override
    public boolean isHacked() {
        return true;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public ModClientType(String name, String modId, String modVersion) {
        this.name = name;
        this.modId = modId;
        this.modVersion = modVersion;
    }
}
