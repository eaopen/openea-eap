package org.openea.eap.cloud.oauth.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(catalog = "ETECH", name = "GE_AUTHORITY")
public class Authority implements GrantedAuthority {

	@Id
	private String id;

	private String name;

	private String code;

	public Authority(String code) {
		this.code = code;
	}

	public Authority() { }

	@Override
	public String getAuthority() {
		return code;
	}

}
