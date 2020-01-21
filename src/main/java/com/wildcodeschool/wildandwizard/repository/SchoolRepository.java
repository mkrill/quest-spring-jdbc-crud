package com.wildcodeschool.wildandwizard.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wildcodeschool.wildandwizard.entity.School;
import com.wildcodeschool.wildandwizard.util.JdbcUtils;

@Repository
public class SchoolRepository implements CrudDao<School> {

	DataSource datasource;

	@Autowired
	public SchoolRepository(DataSource datasource) {
		this.datasource = datasource;
	}

	@Override
	public School save(School school) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;

		try {
			connection = datasource.getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO school (name, capacity, country) VALUES (?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, school.getName());
			statement.setLong(2, school.getCapacity());
			statement.setString(3,  school.getCountry());

			if (statement.executeUpdate() != 1) {
				throw new SQLException("failed to insert school into database");
			}

			generatedKeys = statement.getGeneratedKeys();

			if (generatedKeys.next()) {
				Long id = generatedKeys.getLong(1);
				school.setId(id);
				return school;
			} else {
				throw new SQLException("failed to get inserted school id");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(generatedKeys);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}
		return null;
	}

	@Override
	public School findById(Long id) {

		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultSet=null;

		try {

			connection = datasource.getConnection();
			statement = connection.prepareStatement("select * from school where id = ?;");

			statement.setLong(1, id);
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				String name = resultSet.getString("name");
				Long capacity = resultSet.getLong("capacity");
				String country = resultSet.getString("country");
				return new School (id, name, capacity, country);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}

		return null;
	}

	@Override
	public List<School> findAll() {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = datasource.getConnection();

			statement = connection.prepareStatement("select * from school;");

			resultSet = statement.executeQuery();

			// create return value schools
			List<School> schools = new ArrayList<>();

			while (resultSet.next()) {

				// read column attributes
				String name = resultSet.getString("name");
				Long capacity = resultSet.getLong("capacity");
				String country = resultSet.getString("country");
				Long id = resultSet.getLong("id");

				// create school object
				School school = new School(id, name, capacity, country);

				// add school object to schools list
				schools.add(school);				
			}

			return schools;

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}
		return null;
	}

	@Override
	public School update(School school) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = datasource.getConnection();

			statement = connection.prepareStatement("UPDATE school SET name = ?, capacity = ?, country = ? where id = ?;");

			statement.setString(1, school.getName());
			statement.setLong(2, school.getCapacity());
			statement.setString(3, school.getCountry());
			statement.setLong(4, school.getId());

			if (statement.executeUpdate() != 1) {
				throw new SQLException("failed updating school table");
			} else {
				return school;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}

		return null;
	}

	@Override
	public void deleteById(Long id) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = datasource.getConnection();

			statement = connection.prepareStatement("DELETE FROM school WHERE id = ?;");

			statement.setLong(1, id);

			if (statement.executeUpdate() != 1) {
				throw new SQLException("failed to delete from school table");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(connection);
		}
	}
}
