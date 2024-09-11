package com.example.KTech_Project3.exception;

import com.example.KTech_Project3.entity.CustomUserDetails;
import com.example.KTech_Project3.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {
  public Authentication getAuth() {
      return SecurityContextHolder.getContext().getAuthentication();
  }

  public UserEntity extractUser() {
      CustomUserDetails userDetails = (CustomUserDetails)  getAuth().getPrincipal();
      return  userDetails.getEntity();
  }
}
