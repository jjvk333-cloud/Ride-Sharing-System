package com.velto.model;

import org.springframework.data.annotation.TypeAlias;

/**
 * Concrete User subclass representing a System Administrator.
 */
@TypeAlias("admin")
public class Admin extends User {

    private String department = "OPERATIONS";
    private int adminLevel = 1;

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(String name, String email, String password, String phone) {
        super(name, email, password, Role.ADMIN, phone);
        this.department = "OPERATIONS";
        this.adminLevel = 1;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }
}
