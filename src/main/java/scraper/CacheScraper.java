package scraper;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CacheScraper implements Scraper {
    private Scraper scraper = new DefaultScraper();

    @Override @SneakyThrows
    public Home scrape(String url) {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
        Statement statement = connection.createStatement();

        String request = String.format("select count(*) as count from homes where url='%s'", url);
        ResultSet resultSet = statement.executeQuery(request);
        int num = resultSet.getInt("count");

        if (num <= 0) {
            Home home = scraper.scrape(url);
            String insert = String.format("insert into homes(url, price, beds, baths, garage) :('%s', '%d', '%f', '%f', '%f')",
                    url, home.getPrice(), home.getBeds(), home.getBath(), home.getGarage());
            statement.executeUpdate(insert);
            return  home;
        } else {
            request = String.format("select * from homes where url='%s'", url);
            resultSet = statement.executeQuery(request);
            return new Home(resultSet.getInt("price"), resultSet.getDouble("beds"),
                    resultSet.getDouble("baths"), resultSet.getDouble("garages") );
        }
    }
}