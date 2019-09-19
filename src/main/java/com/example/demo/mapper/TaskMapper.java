package com.example.demo.mapper;

import com.example.demo.domian.dataobject.TaskDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


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
    TaskDo getTaskDoById(Integer id);

    /**
     * 查询所有未处理及要存在内存中的任务
     *
     * @param status
     * @param date
     * @return
     */
    List<TaskDo> getAllByExecuteTimeBeforeAndStatus(Date date, Integer status);

}
