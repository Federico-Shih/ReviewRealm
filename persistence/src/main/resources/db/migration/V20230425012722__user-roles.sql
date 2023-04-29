CREATE TABLE IF NOT EXISTS roles (
    roleId SERIAL PRIMARY KEY,
    roleName varchar(20)
);

CREATE TABLE IF NOT EXISTS user_roles (
  userId int REFERENCES users(id),
  roleId int REFERENCES roles(roleId),
  constraint USER_ROLES_CONS unique (userId, roleId)
);

CREATE INDEX userroles_index ON user_roles(userId);