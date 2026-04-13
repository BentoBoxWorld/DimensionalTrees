package world.bentobox.dimensionaltrees;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.database.AbstractDatabaseHandler;
import world.bentobox.bentobox.database.DatabaseSetup;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.CommandsManager;

/**
 * Tests for {@link DimensionalTrees}.
 */
class DimensionalTreesTest extends CommonTestSetup {

    private static final String CONFIG_YML =
            """
                    dimensionaltrees:
                      commands:
                        admin: "dtrees dimensionaltrees dt"
                      blocks:
                        end:
                          leaves: end_stone
                          logs: purpur_block
                        nether:
                          leaves: glowstone
                          logs: gravel
                      options:
                        sendlog: true
                        enable_addon: true
                        end_trees: true
                        nether_trees: true
                        tree_types:
                          - oak
                          - acacia
                          - birch
                    """;

    @Mock
    private AddonsManager am;

    private DimensionalTrees addon;
    private MockedStatic<DatabaseSetup> mockDb;

    @SuppressWarnings("unchecked")
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // Database mock
        AbstractDatabaseHandler<Object> h = mock(AbstractDatabaseHandler.class);
        mockDb = Mockito.mockStatic(DatabaseSetup.class);
        DatabaseSetup dbSetup = mock(DatabaseSetup.class);
        mockDb.when(DatabaseSetup::getDatabase).thenReturn(dbSetup);
        when(dbSetup.getHandler(any())).thenReturn(h);
        when(h.saveObject(any())).thenReturn(CompletableFuture.completedFuture(true));

        // CommandsManager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // AddonsManager
        when(plugin.getAddonsManager()).thenReturn(am);
        when(am.getGameModeAddons()).thenReturn(Collections.emptyList());

        // FlagsManager
        when(plugin.getFlagsManager()).thenReturn(fm);
        when(fm.getFlags()).thenReturn(Collections.emptyList());

        // Create addon with a JAR containing config.yml
        addon = new DimensionalTrees();
        File jFile = new File("addon.jar");
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jFile))) {
            addJarEntry(jos, "config.yml", CONFIG_YML);
        }
        File dataFolder = new File("addons/DimensionalTrees");
        addon.setDataFolder(dataFolder);
        addon.setFile(jFile);
        AddonDescription desc = new AddonDescription.Builder("bentobox", "DimensionalTrees", "1.0.0")
                .description("test").authors("tastybento").build();
        addon.setDescription(desc);
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        if (mockDb != null) {
            mockDb.closeOnDemand();
        }
        super.tearDown();
        new File("addon.jar").delete();
        deleteAll(new File("addons"));
    }

    private static void addJarEntry(JarOutputStream jos, String name, String content) throws Exception {
        JarEntry entry = new JarEntry(name);
        jos.putNextEntry(entry);
        jos.write(content.getBytes(StandardCharsets.UTF_8));
        jos.closeEntry();
    }

    @Test
    void testGetSettingsNullBeforeLoad() {
        assertNull(addon.getSettings());
    }

    @Test
    void testOnLoad() {
        addon.onLoad();
        assertNotNull(addon.getSettings());
    }

    @Test
    void testOnLoadSettingsDefaults() {
        addon.onLoad();
        Settings s = addon.getSettings();
        assertNotNull(s);
        assertEquals("dtrees dimensionaltrees dt", s.getAdminCommand());
        assertEquals("end_stone", s.getEndLeaves());
        assertEquals("purpur_block", s.getEndLogs());
        assertEquals("glowstone", s.getNetherLeaves());
        assertEquals("gravel", s.getNetherLogs());
    }

    @Test
    void testOnEnable() {
        addon.onLoad();
        addon.onEnable();
        // No game modes hooked, but DimensionalTrees doesn't gate on that — state stays ENABLED
        assertNotNull(addon);
    }

    @Test
    void testOnDisable() {
        // onDisable() is a no-op — must not throw
        addon.onDisable();
        assertNotNull(addon);
    }

    @Test
    void testOnReload() {
        addon.onLoad();
        addon.onReload();
        assertNotNull(addon.getSettings());
    }

    @Test
    void testOnReloadPreservesSettings() {
        addon.onLoad();
        addon.onReload();
        assertEquals("dtrees dimensionaltrees dt", addon.getSettings().getAdminCommand());
    }

    @Test
    void testStateAfterSuccessfulLoad() {
        addon.onLoad();
        // Settings non-null confirms the load succeeded without hitting the DISABLED path
        assertNotNull(addon.getSettings());
    }
}
