package com.yuanyu.ai.tool;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.yuanyu.ai.entity.po.Course;
import com.yuanyu.ai.entity.po.CourseReservation;
import com.yuanyu.ai.entity.po.School;
import com.yuanyu.ai.entity.query.CourseQuery;
import com.yuanyu.ai.service.ICourseReservationService;
import com.yuanyu.ai.service.ICourseService;
import com.yuanyu.ai.service.ISchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseTools {

    private final ICourseService courseService;

    private final ISchoolService schoolService;

    private final ICourseReservationService courseReservationService;

    @Tool(description = "根据条件查询课程")
    public List<Course> queryCourse(@ToolParam(description = "查询的条件") CourseQuery query) {
        if (query == null) {
            return courseService.list(); // 查询所有课程
        }
        // 创建查询条件
        QueryChainWrapper<Course> wrapper = courseService.query()
                .eq(query.getType() != null, "type", query.getType()) // type = '编程'
                .le(query.getEdu() != null, "edu", query.getEdu());// edu <= 2
        // 排序
        if (query.getSorts() != null && !query.getSorts().isEmpty()) {
            for (CourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return wrapper.list();
    }

    @Tool(description = "查询所有校区")
    public List<School> querySchool() {
        return schoolService.list();
    }

    @Tool(description = "生成课程预约单,并返回生成的预约单号")
    public Integer generateCourseReservation(
            @ToolParam(description = "预约课程") String courseName,
            @ToolParam(description = "学生姓名") String studentName,
            @ToolParam(description = "联系电话") String contactInfo,
            @ToolParam(description = "预约校区") String school,
            @ToolParam(description = "备注") String remark) {
        CourseReservation courseReservation = new CourseReservation();
        courseReservation.setCourse(courseName);
        courseReservation.setStudentName(studentName);
        courseReservation.setContactInfo(contactInfo);
        courseReservation.setSchool(school);
        courseReservation.setRemark(remark);
        courseReservationService.save(courseReservation);

        return courseReservation.getId();
    }
}
