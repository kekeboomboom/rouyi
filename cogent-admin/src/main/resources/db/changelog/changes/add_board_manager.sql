insert INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible,
                      status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ("板卡管理", 1, 11, "board", "system/board/index", null, 1, 0, "C", 0, 0, "system:board:list", "system-board",
        "admin", null, "", null, "板卡管理");
