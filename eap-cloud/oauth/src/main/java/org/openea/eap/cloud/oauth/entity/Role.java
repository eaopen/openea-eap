package org.openea.eap.cloud.oauth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(catalog = "ETECH", name = "GE_ROLE")
public class Role implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String description;

	@ManyToMany
	@JoinTable(catalog = "ETECH", name = "GE_ROLE_AUTHORITY",
			joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
			inverseJoinColumns = @JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID"))
	private Set<Authority> authorities;

	@ManyToMany
	@JoinTable(catalog = "ETECH", name = "GE_USER_ROLE",
			joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
			inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"))
	private Set<User> users;

	public Role() {
	}

	public Role(Long id) {
		this.id = id;
	}

}
