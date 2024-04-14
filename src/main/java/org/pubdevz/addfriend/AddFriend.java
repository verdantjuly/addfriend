package org.pubdevz.addfriend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddFriend extends JavaPlugin implements Listener {

    private File dataFolder;

    @Override
    public void onEnable() {
        dataFolder = new File(getDataFolder(), "friendList");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        new CommandAPICommand("addfriend")
                .withArguments(new PlayerArgument("user"))
                .executesPlayer((player, args) -> {
                    Player targetPlayer = (Player) args.get("user");
                    if (targetPlayer == null) {
                        player.sendMessage("플레이어를 찾을 수 없습니다.");
                    } else {
                        try {
                            writeFile(targetPlayer.getName(), player.getName(), player);
                        } catch (IOException e) {
                            player.sendMessage("친구 추가 중 오류가 발생했습니다.");
                            e.printStackTrace();
                        }
                    }
                })
                .register();

        new CommandAPICommand("listfriend")
                .executesPlayer((player, args) -> {
                    try {
                        readFile(player, player.getName());
                    } catch (IOException e) {
                        player.sendMessage("친구 목록을 불러오는 중 오류가 발생했습니다.");
                        e.printStackTrace();
                    }
                })
                .register();

        new CommandAPICommand("removefriend")
                .withArguments(new PlayerArgument("user"))
                .executesPlayer((player, args) -> {
                    Player targetPlayer = (Player) args.get("user");
                    if (targetPlayer == null) {
                        player.sendMessage("플레이어를 찾을 수 없습니다.");
                    } else {
                        deleteFile(player, player.getName(), targetPlayer.getName());
                    }
                })
                .register();
    }


    private void writeFile(String username, String playerName, Player player) throws IOException {

        String directoryPath = "./plugins/addFriend/friendList/";
        File directory = new File(directoryPath);
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



