package per.qiang.common.security.pojo;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import per.qiang.common.core.pojo.UserWrapper;

import java.util.Collection;

public class AuthUser extends UserWrapper implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = 5201314L;

    private  Collection<? extends GrantedAuthority> authorities;

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isNotExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getStatus();
    }

    @Override
    public void eraseCredentials() {
        super.setPassword(null);
    }




}
