ALTER TABLE user_roles ADD COLUMN role VARCHAR(30);
UPDATE user_roles SET role = r.rolename FROM roles r WHERE r.roleid = user_roles.roleid;
ALTER TABLE user_roles DROP COLUMN roleid;
DROP TABLE roles;

ALTER TABLE user_disabled_notifications ADD COLUMN notification VARCHAR(50);
UPDATE user_disabled_notifications SET notification = n.notificationtype FROM notifications n WHERE n.notificationid = user_disabled_notifications.notificationid;
ALTER TABLE user_disabled_notifications DROP COLUMN notificationid;
DROP TABLE notifications;

