package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_roleid_seq")
    @SequenceGenerator(sequenceName = "roles_roleid_seq", name = "roles_roleid_seq", allocationSize = 1)
    @Column(name = "roleid")
    private Long roleid;

    @Column(name = "rolename")
    private String roleName;

    public Role(String roleName) {
        this.roleName = roleName;
    }

    /* package */ Role() {
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(roleName, role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

    public Long getRoleid() {
        return roleid;
    }
}
