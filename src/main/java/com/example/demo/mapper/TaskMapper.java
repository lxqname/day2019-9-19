package com.example.demo.mapper;

import com.example.demo.domian.dataobject.TaskDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * @author 86151
 */
@Repository
public interface TaskMapper extends JpaRepository<TaskDo, Integer> {
    /**
     * 根据id查询任务
     *
     * @param id
     * @return
     */
    Optional<TaskDo> getTaskDoById(Integer id);

    /**
     * 查询所有未处理的任务
     * @param date
     * @param status
     * @return
     */
    List<TaskDo> getAllByExecuteTimeBeforeAndStatus(Date date,Integer status);

}
