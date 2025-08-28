package com.osc.oscms.materialservice.client;

import com.osc.oscms.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 课程服务Feign客户端
 * 用于资料服务调用课程服务获取课程信息
 */
@FeignClient(name = "course-service", path = "/api")
public interface CourseServiceClient {

    /**
     * 获取课程信息
     */
    @GetMapping("/courses/{courseId}")
    ApiResponse<Map<String, Object>> getCourseById(@PathVariable("courseId") Long courseId);

    /**
     * 获取班级信息
     */
    @GetMapping("/classes/{classId}")
    ApiResponse<Map<String, Object>> getClassById(@PathVariable("classId") Long classId);

    /**
     * 验证用户是否有课程访问权限
     */
    @GetMapping("/courses/{courseId}/users/{userId}/permission")
    ApiResponse<Boolean> hasUserCoursePermission(@PathVariable("courseId") Long courseId, 
                                               @PathVariable("userId") String userId);
}
