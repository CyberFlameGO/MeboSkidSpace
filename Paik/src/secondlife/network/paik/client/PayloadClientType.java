package secondlife.network.paik.client;

import lombok.Getter;

@Getter
public class PayloadClientType implements ClientType {
    
    private String name;
    private String payload;
    private boolean hacked;
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isHacked() {
        return this.hacked;
    }
    
    public PayloadClientType(String name, String payload, boolean hacked) {
        this.name = name;
        this.payload = payload;
        this.hacked = hacked;
    }
}
