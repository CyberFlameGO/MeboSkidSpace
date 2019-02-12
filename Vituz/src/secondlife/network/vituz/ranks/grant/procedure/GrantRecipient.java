package secondlife.network.vituz.ranks.grant.procedure;

import java.util.UUID;

import lombok.Getter;

@Getter
public class GrantRecipient {
	
	private String name;

	public GrantRecipient(String name) {
		this.name = name;
	}
}
