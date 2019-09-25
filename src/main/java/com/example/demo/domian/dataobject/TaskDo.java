package com.example.demo.domian.dataobject;


import javax.persistence.*;

import java.util.Date;

/**
 * @author 86151
 */
@Entity
@Table(name = "task")
public class TaskDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "method")
    private String method;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "data")
    private String data;

    @Column(name = "url")
    private String url;

    @Column(name = "status")
    private Integer status;

    @Column(name = "execute_time")
    private Date executeTime;

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskDo{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", createTime=" + createTime +
                ", data='" + data + '\'' +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", executeTime=" + executeTime +
                '}';
    }
}
