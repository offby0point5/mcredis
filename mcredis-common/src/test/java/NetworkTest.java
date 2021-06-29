import com.github.offby0point5.mcredis.Network;
import com.github.offby0point5.mcredis.datatype.ItemStack;
import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.Party;
import com.github.offby0point5.mcredis.Player;
import com.github.offby0point5.mcredis.Server;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;
import org.junit.AfterClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NetworkTest {
    String serverName = "lobby2";
    UUID playerUUID = UUID.fromString("9296a07e-4b27-4582-90d3-1b31ac54be7a");
    UUID partyUUID = UUID.fromString("474d2976-7369-499b-8a81-c4860cdc3441");

    @AfterClass
    public static void cleanup() {
        System.out.println("Clean up.");
        try (Jedis jedis = Network.getJedis()) {
            Set<String> keys = jedis.keys(String.format("%s:*", Network.NETWORK_PREFIX));
            Transaction transaction = jedis.multi();
            for (String key : keys)
                transaction.del(key);
            transaction.exec();
        }
        System.out.println("Done.");
    }

    @Test
    public void checkServer() {
        Server lobby = new Server(serverName);
        lobby.delete();
        assertNull(lobby.getAddress());
        assertNull(lobby.getMain());
        assertNull(lobby.getStatus());

        String mainGroup = "lobby";
        lobby.setMain(mainGroup);
        assertEquals(mainGroup, lobby.getMain());

        String[] groups = {"lobby", "fallback"};
        lobby.addGroups(groups);
        assertEquals(Set.of(groups), lobby.getGroups());

        InetSocketAddress address = new InetSocketAddress("localhost", 25600);
        lobby.setAddress(address);
        assertEquals(address, lobby.getAddress());

        lobby.delete();
        assertNull(lobby.getAddress());
        assertNull(lobby.getMain());
        assertNull(lobby.getStatus());
    }

    @Test
    public void checkPlayer() {
        Player player = new Player(playerUUID);
        player.delete();
        assertNull(player.getServer());
        assertNull(player.getParty());

        player.joinServer(serverName);
        assertEquals(serverName, player.getServer());

        player.delete();
        assertNull(player.getServer());
        assertNull(player.getParty());
    }

    @Test
    public void checkParty() {
        Party party = new Party(partyUUID);
        party.delete();
        assertNull(party.getLeader());
        assertEquals(Collections.emptySet(), party.getMembers());
    }

    @Test
    public void checkGroup() {
        Group group = new Group("lobby");
        group.delete();
        assertNull(group.getKickRule());
        assertNull(group.getJoinRule());
        assertNull(group.getItem());
        assertEquals(Collections.emptySet(), group.getMembers());

        group.setJoinRule(JoinRules.NONE);
        assertEquals(JoinRules.NONE, group.getJoinRule());

        group.setKickRule(KickRules.NONE);
        assertEquals(KickRules.NONE, group.getKickRule());
    }

    @Test
    public void checkGroupItem() {
        Group group = new Group("random");
        group.delete();

        ItemStack itemStack = new ItemStack.Builder("BARRIER", "ITEM for test...")
                .amount(2)
                .glowing(true)
                .lore(List.of("Line1", "Line2"))
                .build();

        String item = itemStack.serialize();
        assertEquals(item, ItemStack.deserialize(item).serialize());
        assertEquals(itemStack, ItemStack.deserialize(item));

        group.setItem(itemStack);
        assertEquals(itemStack, group.getItem());
    }

    @Test
    public void checkNetworkMethods() throws InterruptedException {
        Server server = new Server("server");
        server.setMain("group");
        server.setAddress(new InetSocketAddress(25600));
        Player player = new Player(playerUUID);
        player.joinServer("server");
        player.joinParty(partyUUID);

        TimeUnit.SECONDS.sleep(2);
        assertEquals(Set.of("group"), Network.getGroups());
        assertEquals(Set.of("server"), Network.getServers());
        assertEquals(Set.of(playerUUID), Network.getPlayers());
        assertEquals(Set.of(partyUUID), Network.getParties());

    }
}
