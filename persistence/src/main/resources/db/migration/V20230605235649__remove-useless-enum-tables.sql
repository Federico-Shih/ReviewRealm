UPDATE user_roles SET role = r.rolename FROM roles r WHERE r.roleid = user_roles.roleid;

UPDATE user_disabled_notifications SET notification = n.notificationtype FROM notifications n WHERE n.notificationid = user_disabled_notifications.notificationid;


