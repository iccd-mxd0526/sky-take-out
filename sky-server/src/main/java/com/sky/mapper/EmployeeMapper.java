package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /*
    * 插入员工数据
    *
    * */
    @Insert("insert into employee( name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "values" +
            "( #{name}, #{username}, #{password}, #{phone}, #{sex},#{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    //这里id是自增的，所以不需要插入
    void insert(Employee employee);

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /*
    *分页查询
    *
    * */
    //需要用到动态标签,所以不使用注解,使用xml进行映射
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键动态修改Status
     * @param employee
     */

    void update(Employee employee);

    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
