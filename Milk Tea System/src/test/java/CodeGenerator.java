import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {


    // 1. 数据库连接配置 (改成你自己的数据库)
    private static final String URL = "jdbc:mysql://localhost:3306/milktea?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";


    // 新增：读取所有表名的方法（仅新增这个方法，不修改原有逻辑）
    private static String[] getAllTableNames() {
        List<String> tableNames = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();
            String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }
        } catch (Exception e) {
            throw new RuntimeException("读取表名失败", e);
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return tableNames.toArray(new String[0]);
    }

    // 2. 你的项目根路径 (通常不需要改，除非是多模块项目)
    private static final String PROJECT_PATH = System.getProperty("user.dir");

    public static void main(String[] args) {

        String[] allTableNames = getAllTableNames();

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                // ===================== 1. 全局配置 =====================
                .globalConfig(builder -> {
                    builder.author("韦宇翔")
                            .dateType(DateType.TIME_PACK)
                            .commentDate("yyyy-MM-dd")
                            .outputDir(PROJECT_PATH + "/src/main/java");
                })

                // ===================== 2. 【核心】包配置 (修正路径的关键) =====================
                .packageConfig(builder -> {
                    builder.parent("com.example") // 父包名
                            .moduleName("milkteasystem") // 【关键】模块名，这样生成的包就是 com.example.milkteasystem
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            // 设置 Mapper XML 生成路径
                            .pathInfo(Collections.singletonMap(OutputFile.xml, PROJECT_PATH + "/src/main/resources/mapper"));
                })

                // ===================== 3. 策略配置 =====================
                .strategyConfig(builder -> {
                    builder.addInclude(allTableNames)

                            .addTablePrefix("t_") // 如果你的表名是 t_user 这种带前缀的，解开注释并填前缀，生成的实体类会自动去掉 t_

                            // 3.1 实体类策略
                            .entityBuilder()
                            .enableLombok() // 开启 Lombok
                            .idType(IdType.ASSIGN_ID) // 主键类型：雪花算法
                            .naming(NamingStrategy.underline_to_camel) // 下划线转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .logicDeleteColumnName("deleted") // 如果有逻辑删除字段，填这里
                            .versionColumnName("version") // 如果有乐观锁字段，填这里
                            // 自动填充配置 (可选)
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))

                            // 3.2 Mapper 策略
                            .mapperBuilder()
                            .enableMapperAnnotation()
                            .enableBaseResultMap()
                            .enableBaseColumnList()

                            // 3.3 Service 策略
                            .serviceBuilder()
                            // .disableServiceInterface() // 【可选】如果不想要 IUserService 这种带 I 前缀的接口，解开这行注释

                            // 3.4 Controller 策略
                            .controllerBuilder()
                            .enableRestStyle()
                            .enableHyphenStyle();// 新增：URL驼峰转连字符

                })

                // 4. 模板引擎
                .templateEngine(new FreemarkerTemplateEngine())

                // 5. 执行生成
                .execute();
    }
}