package com.example.codeleapbackend.project;

import com.example.codeleapbackend.project.pojo.entity.Project;
import com.example.codeleapbackend.project.pojo.vo.ProjectVO;
import com.example.codeleapbackend.project.service.ProjectService;
import com.example.codeleapbackend.user.controller.UserController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Opt in with a fresh, disposable MySQL database; never use the application database. */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@EnabledIfEnvironmentVariable(named = "PROJECT_MYSQL_TEST_URL", matches = ".+")
class ProjectDataMySqlTests {
    private static final long OWNER_A = 91001L;
    private static final long OWNER_B = 91002L;
    private static final LocalDateTime EARLY = LocalDateTime.of(2026, 10, 20, 10, 0);
    private static final LocalDateTime LATE = LocalDateTime.of(2026, 10, 20, 11, 0);

    @Autowired private JdbcTemplate jdbc;
    @Autowired private ProjectService service;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("PROJECT_MYSQL_TEST_URL"));
        registry.add("spring.datasource.username", () -> envOrDefault("PROJECT_MYSQL_TEST_USERNAME", "root"));
        registry.add("spring.datasource.password", () -> envOrDefault("PROJECT_MYSQL_TEST_PASSWORD", ""));
    }

    private static String envOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null ? fallback : value;
    }

    @BeforeAll
    void installSchemaOnlyInEmptyTestDatabase() throws Exception {
        String database = jdbc.queryForObject("SELECT DATABASE()", String.class);
        assertThat(database).as("refuse DDL in a non-test database")
                .startsWith("codeleap_project_test_");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = DATABASE()", Long.class))
                .as("use a NEW empty test database for each run").isZero();
        assertThat(jdbc.queryForObject("SELECT @@SESSION.sql_mode", String.class))
                .as("length and NOT NULL checks require MySQL strict mode").contains("STRICT_");

        Path script = Path.of("..", "docs", "project-v0.sql");
        if (!Files.isRegularFile(script)) {
            script = Path.of("docs", "project-v0.sql");
        }
        try (Connection connection = jdbc.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(connection, new FileSystemResource(script));
        }
    }

    @Test
    void schemaMatchesFiveFieldsAndOwnerTimeIndex() {
        assertThat(jdbc.queryForList("SELECT column_name FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = 'project' "
                + "ORDER BY ordinal_position", String.class))
                .containsExactly("id", "name", "description", "owner_id", "create_time");
        for (String required : List.of("id", "name", "owner_id", "create_time")) {
            assertThat(columnValue(required, "is_nullable")).isEqualTo("NO");
        }
        assertThat(columnValue("description", "is_nullable")).isEqualTo("YES");
        assertThat(columnValue("id", "data_type")).isEqualTo("bigint");
        assertThat(columnValue("owner_id", "data_type")).isEqualTo("bigint");
        assertThat(columnValue("create_time", "data_type")).isEqualTo("datetime");
        assertThat(columnValue("id", "column_key")).isEqualTo("PRI");
        assertThat(columnValue("id", "extra")).contains("auto_increment");
        assertThat(columnValue("name", "column_type")).isEqualTo("varchar(100)");
        assertThat(columnValue("description", "column_type")).isEqualTo("varchar(500)");
        assertThat(jdbc.queryForList("SELECT GROUP_CONCAT(column_name ORDER BY seq_in_index) "
                + "FROM information_schema.statistics WHERE table_schema = DATABASE() "
                + "AND table_name = 'project' AND non_unique = 1 GROUP BY index_name", String.class))
                .contains("owner_id,create_time");
        assertThat(jdbc.queryForObject("SELECT engine FROM information_schema.tables "
                + "WHERE table_schema = DATABASE() AND table_name = 'project'", String.class))
                .isEqualTo("InnoDB");
        assertThat(jdbc.queryForObject("SELECT table_collation FROM information_schema.tables "
                + "WHERE table_schema = DATABASE() AND table_name = 'project'", String.class))
                .startsWith("utf8mb4_");
    }

    private String columnValue(String column, String metadataColumn) {
        // metadataColumn is supplied only by the literal assertions above, never by user input.
        return jdbc.queryForObject("SELECT " + metadataColumn + " FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = 'project' AND column_name = ?",
                String.class, column);
    }

    @Test
    void saveFillsGeneratedIdAndMapsOwnerAndTimeToDatabaseColumns() {
        Project project = project(OWNER_A, "项目🙂", null, EARLY);
        assertThat(service.save(project)).isTrue();
        assertThat(project.getId()).isPositive();
        Project loaded = service.getById(project.getId());
        assertThat(loaded.getName()).isEqualTo("项目🙂");
        assertThat(loaded.getDescription()).isNull();
        assertThat(loaded.getOwnerId()).isEqualTo(OWNER_A);
        assertThat(loaded.getCreateTime()).isEqualTo(EARLY);
        assertThat(jdbc.queryForObject("SELECT owner_id FROM project WHERE id = ?", Long.class,
                project.getId())).isEqualTo(OWNER_A);
        assertThat(jdbc.queryForObject("SELECT create_time FROM project WHERE id = ?",
                LocalDateTime.class, project.getId())).isEqualTo(EARLY);
    }

    @Test
    void listFiltersOwnerAndOrdersByCreationTimeDescending() {
        assertThat(service.save(project(OWNER_A, "A-old", null, EARLY))).isTrue();
        assertThat(service.save(project(OWNER_B, "B-private", null, LATE.plusHours(1)))).isTrue();
        assertThat(service.save(project(OWNER_A, "A-new", "description", LATE))).isTrue();
        assertThat(service.listByOwner(OWNER_A)).extracting(ProjectVO::getName)
                .containsExactly("A-new", "A-old");
        assertThat(service.listByOwner(OWNER_B)).extracting(ProjectVO::getName)
                .containsExactly("B-private");
        assertThat(service.listByOwner(91999L)).isEmpty();
    }

    @Test
    void databaseRejectsMissingAndNullRequiredValues() {
        for (String sql : List.of(
                "INSERT INTO project (owner_id, create_time) VALUES (91001, '2026-10-20 10:00:00')",
                "INSERT INTO project (name, create_time) VALUES ('missing-owner', '2026-10-20 10:00:00')",
                "INSERT INTO project (name, owner_id) VALUES ('missing-time', 91001)")) {
            assertSqlRejected(1364, sql);
        }
        for (String sql : List.of(
                "INSERT INTO project (name, owner_id, create_time) VALUES (NULL, 91001, '2026-10-20 10:00:00')",
                "INSERT INTO project (name, owner_id, create_time) VALUES ('null-owner', NULL, '2026-10-20 10:00:00')",
                "INSERT INTO project (name, owner_id, create_time) VALUES ('null-time', 91001, NULL)")) {
            assertSqlRejected(1048, sql);
        }
        assertThat(service.count()).isZero();
    }

    @Test
    void databaseAcceptsLengthLimitsAndRejectsOverflow() {
        Project boundary = project(OWNER_A, "n".repeat(100), "d".repeat(500), EARLY);
        assertThat(service.save(boundary)).isTrue();
        assertThat(service.getById(boundary.getId()).getName()).hasSize(100);
        assertThat(service.getById(boundary.getId()).getDescription()).hasSize(500);
        assertSqlRejected(1406,
                "INSERT INTO project (name, owner_id, create_time) VALUES (?, ?, ?)",
                "n".repeat(101), OWNER_A, EARLY);
        assertSqlRejected(1406,
                "INSERT INTO project (name, description, owner_id, create_time) VALUES (?, ?, ?, ?)",
                "short-name", "d".repeat(501), OWNER_A, EARLY);
        assertThat(service.count()).isEqualTo(1);
    }

    @Test
    void httpCreationUsesSessionOwnerAndDoesNotExposeOwnerId() throws Exception {
        String response = mvc.perform(post("/api/v1/projects").session(session(OWNER_A))
                        .contentType("application/json")
                        .content("{\"name\":\"  A-created  \",\"description\":\"A description\","
                                + "\"ownerId\":91002,\"id\":777,\"createTime\":\"2000-01-01T00:00:00\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(2000))
                .andExpect(jsonPath("$.data.name").value("A-created"))
                .andExpect(jsonPath("$.data.ownerId").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        JsonNode created = objectMapper.readTree(response).get("data");
        Project stored = service.getById(created.get("id").asLong());
        assertThat(stored.getOwnerId()).isEqualTo(OWNER_A);
        assertThat(stored.getId()).isNotEqualTo(777L);
        assertThat(stored.getDescription()).isEqualTo("A description");
        assertThat(stored.getCreateTime()).isAfter(LocalDateTime.of(2000, 1, 1, 0, 0));
        mvc.perform(post("/api/v1/projects").session(session(OWNER_B))
                        .contentType("application/json").content("{\"name\":\"B-created\"}"))
                .andExpect(jsonPath("$.code").value(2000));
        assertThat(service.listByOwner(OWNER_B)).extracting(ProjectVO::getName)
                .containsExactly("B-created");
        mvc.perform(get("/api/v1/projects").session(session(OWNER_A)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("A-created"))
                .andExpect(jsonPath("$.data[0].ownerId").doesNotExist());
    }

    @Test
    void unauthenticatedRequestsAreRejectedWithoutWritingRows() throws Exception {
        mvc.perform(get("/api/v1/projects")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4000));
        mvc.perform(post("/api/v1/projects").contentType("application/json")
                        .content("{\"name\":\"anonymous\"}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(4000));
        assertThat(service.count()).isZero();
    }

    @Test
    void invalidRequestsDoNotWriteRows() throws Exception {
        for (Map<String, String> input : List.of(
                Map.<String, String>of(), Map.of("name", ""), Map.of("name", "   "),
                Map.of("name", "n".repeat(101)),
                Map.of("name", "valid", "description", "d".repeat(501)))) {
            mvc.perform(post("/api/v1/projects").session(session(OWNER_A))
                            .contentType("application/json").content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(5002));
        }
        assertThat(service.count()).isZero();
    }

    private void assertSqlRejected(int mysqlErrorCode, String sql, Object... parameters) {
        DataAccessException failure = assertThrows(DataAccessException.class,
                () -> jdbc.update(sql, parameters), sql);
        assertThat(failure.getRootCause()).isInstanceOf(SQLException.class);
        assertThat(((SQLException) failure.getRootCause()).getErrorCode()).isEqualTo(mysqlErrorCode);
    }

    private static Project project(long ownerId, String name, String description, LocalDateTime time) {
        Project project = new Project();
        project.setOwnerId(ownerId);
        project.setName(name);
        project.setDescription(description);
        project.setCreateTime(time);
        return project;
    }

    private static MockHttpSession session(long ownerId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(UserController.SESSION_USER_ID, ownerId);
        return session;
    }
}
