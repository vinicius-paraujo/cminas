package com.markineo.cminas.control;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.markineo.cminas.util.DatabaseManager;
import com.markineo.cminas.util.FileManager;

public class MinasManager {
	private FileManager fManager;
	private HashMap<UUID, Integer> blocksBreaks;
	
	public MinasManager(FileManager fManager) {
		this.fManager = fManager;
		this.blocksBreaks = new HashMap<>();
	}
	
	private Connection conn;
	private ResultSet rs;
	
	public int getPlayerPickaxeLevel(Player player) {
		try {
			conn = DatabaseManager.getConnection();
			rs = DatabaseManager.executeQueryRs(conn, "SELECT * FROM minasPlayers WHERE uuid=?", player.getUniqueId().toString());
			
			if (!rs.next()) return -1;
			return rs.getInt("pickaxe_level");
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getBlocksBreaks(UUID playerUUID) {
        return blocksBreaks.get(playerUUID) != null ? blocksBreaks.get(playerUUID) : 0;
    }
	
	public void resetBlockBreak(Player player) {
        UUID uuid = player.getUniqueId();
        blocksBreaks.put(uuid, 0);
	}
	
	public void updateBlockBreak(Player player, int size) {
        UUID uuid = player.getUniqueId();
        blocksBreaks.put(uuid, size);
	}
	
	public void updatePickaxeLevel(Player player, int level) {
		try {
			conn = DatabaseManager.getConnection();
			DatabaseManager.executeQuery(conn, "UPDATE minasPlayers SET pickaxe_level=? WHERE uuid=?", level, player.getUniqueId().toString());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateBlockBreakDatabase() {
        try {
            conn = DatabaseManager.getConnection();
            for (Map.Entry<UUID, Integer> entry : blocksBreaks.entrySet()) {
                UUID uuid = entry.getKey();
                int blockBreaks = entry.getValue();
                
                DatabaseManager.executeQuery(conn, "UPDATE minasPlayers SET blockBreak=? WHERE uuid=?", blockBreaks, uuid.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	public boolean isPlayerRegistred(Player player) {
		try {
			conn = DatabaseManager.getConnection();
			rs = DatabaseManager.executeQueryRs(conn, "SELECT * FROM minasPlayers WHERE uuid=?", player.getUniqueId().toString());
			
			if (!rs.next()) return false;
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void registerPlayer(Player player) {
		try {
			conn = DatabaseManager.getConnection();
			DatabaseManager.executeQuery(conn, "INSERT INTO minasPlayers (uuid, pickaxe_level, blockBreak) VALUES (?,?,?)", player.getUniqueId().toString(), 1, 0);
			blocksBreaks.put(player.getUniqueId(), 0);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
