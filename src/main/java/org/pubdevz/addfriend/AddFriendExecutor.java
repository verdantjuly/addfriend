package org.pubdevz.addfriend;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddFriendExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();
        String username = null;
        Player targetPlayer = null;


        switch (command.getName().toLowerCase()) {
            case "addfriend" :
                username = args[0];
                targetPlayer = Bukkit.getPlayerExact(username);
                if (targetPlayer == null) {
                    player.sendMessage(username + " 플레이어를 찾을 수 없습니다.");
                    return true;
                }

                try {
                    writeFile(targetPlayer.getName(), playerName, player);

                } catch (IOException e) {
                    player.sendMessage("친구 추가 중 오류가 발생했습니다.");
                    e.printStackTrace();
                }
                return true;
            case "listfriend":
                try {
                    username = args[0];
                    readFile(player, playerName);
                } catch (IOException e) {
                    player.sendMessage("친구 목록을 불러오는 중 오류가 발생했습니다.");
                    e.printStackTrace();
                }
                return true;
            case "removefriend":
                deleteFile(player, playerName, username);
                return true;
            default:
                return false;
        }
    }

    private void writeFile(String username, String playerName, Player player) throws IOException {

        String directoryPath = "./plugins/addFriend/friendList/";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, playerName + ".txt");
        List<String> existingFriends = new ArrayList<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    existingFriends.add(line.trim());
                }
            }
        }

        if (existingFriends.contains(username)) {
            player.sendMessage(username + "님은 이미 친구 목록에 있습니다.");
        } else {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(username);
            writer.newLine();
            player.sendMessage(username + "님이 친구로 등록되었습니다.");
        }}
    }

    private void readFile(Player player, String playerName) throws IOException {
        String filePath = "./plugins/addFriend/friendList/" + playerName + ".txt";
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            player.sendMessage("친구 목록이 비어 있습니다.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            boolean hasFriends = false;
            while ((line = reader.readLine()) != null) {
                player.sendMessage(line);
                hasFriends = true;
            }
            if (!hasFriends) {
                player.sendMessage("친구 목록이 비어 있습니다.");
            }
        } catch (IOException e) {
            player.sendMessage("친구 목록을 불러오는 데 실패했습니다.");
            throw e;
        }
    }

    private void deleteFile(Player player, String playerName, String username) {
        String filePath = "./plugins/addFriend/friendList/" + playerName + ".txt";
        File file = new File(filePath);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equalsIgnoreCase(username))
                    .collect(Collectors.toList());

            Files.write(file.toPath(), updatedLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            player.sendMessage(username + "님이 친구 목록에서 삭제되었습니다.");
        } catch (IOException e) {
            player.sendMessage("친구 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}
