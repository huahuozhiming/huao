<template>
  <el-container>
    <el-header>
      <h3>教师端</h3>
      <div>
        <span>{{ userInfo.name }} ({{ userInfo.id }})</span>
        <el-button @click="logout">退出</el-button>
      </div>
    </el-header>
    <el-main>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 数据统计（第一个标签页） -->
        <el-tab-pane label="数据统计" name="statistics" :lazy="true">
          <div v-loading="statisticsLoading" class="statistics-container">
            <el-row :gutter="20" class="stat-cards-row">
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <div class="stat-card-icon"><i class="el-icon-notebook-2"></i></div>
                  <div class="stat-card-content">
                    <div class="stat-card-value">{{ statisticsData.courseTotal || 0 }}</div>
                    <div class="stat-card-label">课程总数</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <div class="stat-card-icon"><i class="el-icon-document"></i></div>
                  <div class="stat-card-content">
                    <div class="stat-card-value">{{ statisticsData.workTotal || 0 }}</div>
                    <div class="stat-card-label">作业总数</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <div class="stat-card-icon"><i class="el-icon-files"></i></div>
                  <div class="stat-card-content">
                    <div class="stat-card-value">{{ statisticsData.resourceTotal || 0 }}</div>
                    <div class="stat-card-label">资源总数</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <div class="stat-card-icon"><i class="el-icon-user-solid"></i></div>
                  <div class="stat-card-content">
                    <div class="stat-card-value">{{ statisticsData.studentTotal || 0 }}</div>
                    <div class="stat-card-label">学生人次</div>
                  </div>
                </div>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="chart-row">
              <el-col :xs="24" :sm="24">
                <div class="chart-box">
                  <div class="chart-title">课程状态分布</div>
                  <div ref="courseStatusChart" style="height: 320px;"></div>
                </div>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="chart-row">
              <el-col :xs="24" :sm="12">
                <div class="chart-box">
                  <div class="chart-title">作业批阅状态分布</div>
                  <div ref="workStatusChart" style="height: 320px;"></div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12">
                <div class="chart-box">
                  <div class="chart-title">资源类型占比</div>
                  <div ref="resourceTypeChart" style="height: 320px;"></div>
                </div>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="chart-row">
              <el-col :xs="24" :sm="24">
                <div class="chart-box">
                  <div class="chart-title">作业分数分布（已批阅作业）</div>
                  <div ref="scoreDistributionChart" style="height: 350px;"></div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 课程管理 -->
        <el-tab-pane label="课程管理" name="courses" :lazy="true">
          <el-form :inline="true" :model="courseQuery">
            <el-form-item label="课程号">
              <el-input v-model="courseQuery.id" placeholder="课程号" />
            </el-form-item>
            <el-form-item label="课程名">
              <el-input v-model="courseQuery.name" placeholder="课程名" />
            </el-form-item>
            <el-form-item label="课程状态">
              <el-select v-model="courseQuery.courseStatus" clearable placeholder="全部">
                <el-option label="进行中" :value="0" />
                <el-option label="批阅中" :value="1" />
                <el-option label="已完成" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchCourses">搜索</el-button>
              <el-button @click="showAddCourseDialog">新增课程</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="courseList" border>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="studentTotal" label="学生人数" width="90" align="center" />
            <el-table-column prop="workTotal" label="作业总数" width="90" align="center" />
            <el-table-column label="状态" width="100" align="center">
              <template slot-scope="scope">
                {{ ['进行中', '批阅中', '已完成'][scope.row.courseStatus] || '未知' }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column prop="termPeriod" label="学期" width="100" />
            <el-table-column label="操作" width="220">
              <template slot-scope="scope">
                <el-button size="mini" @click="editCourse(scope.row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="deleteCourse(scope.row.id)">删除</el-button>
                <el-button size="mini" type="primary" @click="viewCourseWorks(scope.row)">作业</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination background layout="prev, pager, next" :current-page="courseQuery.current"
            :page-size="coursePage.pageSize" :total="courseTotal"
            @current-change="val => { courseQuery.current = val; loadCourses() }">
          </el-pagination>
        </el-tab-pane>

        <!-- 作业管理 -->
        <el-tab-pane label="作业管理" name="works">
          <el-tabs v-model="workSubTab" @tab-click="handleWorkSubTabClick">
            <!-- 学生作业 -->
            <el-tab-pane label="学生作业" name="studentWorks">
              <el-form :inline="true" :model="studentWorkQuery">
                <el-form-item label="作业ID">
                  <el-input v-model="studentWorkQuery.id" placeholder="作业ID" />
                </el-form-item>
                <el-form-item label="课程ID">
                  <el-input v-model="studentWorkQuery.courseId" placeholder="课程ID" />
                </el-form-item>
                <el-form-item label="作业名">
                  <el-input v-model="studentWorkQuery.name" placeholder="作业名" />
                </el-form-item>
                <el-form-item label="学生ID">
                  <el-input v-model="studentWorkQuery.studentId" placeholder="学生ID" />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="studentWorkQuery.workStatus" placeholder="请选择状态">
                    <el-option label="未批阅" :value="0" />
                    <el-option label="已批阅" :value="1" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadStudentWorks">查询</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="studentWorkList" border>
                <el-table-column prop="id" label="作业id" width="80" />
                <el-table-column prop="name" label="作业名" min-width="120" />
                <el-table-column prop="courseId" label="课程号" width="80" />
                <el-table-column prop="courseName" label="课程名" min-width="100" />
                <el-table-column prop="studentId" label="学号" width="100" />
                <el-table-column prop="studentName" label="学生姓名" width="100" />
                <el-table-column prop="clazz" label="班级" width="100" />
                <el-table-column label="状态" width="80" align="center">
                  <template slot-scope="scope">
                    <span v-if="scope.row.workStatus === 0">未批阅</span>
                    <span v-else-if="scope.row.workStatus === 1">已批阅</span>
                    <span v-else>未知</span>
                  </template>
                </el-table-column>
                <el-table-column prop="score" label="分数" width="70" align="center" />
                <el-table-column prop="position" label="作业保存路径" min-width="150" show-overflow-tooltip />
                <el-table-column label="操作" width="420">
                  <template slot-scope="scope">
                    <el-button size="mini" @click="openGradeDialog(scope.row)">批阅</el-button>
                    <el-button size="mini" type="success" @click="downloadWork(scope.row)">下载</el-button>
                    <el-button size="mini" type="danger" @click="deleteStudentWork(scope.row.id)">删除</el-button>
                    <!-- AI批阅按钮：传递整行数据 -->
                    <el-button size="mini" type="info" :loading="aiLoadingMap[scope.row.id]"
                      :disabled="aiLoadingMap[scope.row.id]" @click="callAiAnalysis(scope.row)">
                      AI批阅
                    </el-button>
                    <el-button size="mini" type="primary" :disabled="!aiResultMap[scope.row.id]"
                      @click="viewAiResult(scope.row.id, scope.row.name)">
                      查看结果
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination background layout="prev, pager, next" :current-page="studentWorkQuery.current"
                :page-size="workPage.pageSize" :total="studentWorkTotal"
                @current-change="val => { studentWorkQuery.current = val; loadStudentWorks() }">
              </el-pagination>
            </el-tab-pane>

            <!-- 作业设置 -->
            <el-tab-pane label="作业设置" name="settingWorks">
              <el-form :inline="true" :model="settingWorkQuery">
                <el-form-item label="作业ID">
                  <el-input v-model="settingWorkQuery.id" placeholder="作业ID" clearable />
                </el-form-item>
                <el-form-item label="课程ID">
                  <el-input v-model="settingWorkQuery.courseId" placeholder="课程ID" clearable />
                </el-form-item>
                <el-form-item label="作业名">
                  <el-input v-model="settingWorkQuery.name" placeholder="作业名" clearable />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="searchSettingWorks">查询</el-button>
                  <el-button type="success" @click="showPublishWorkDialog">发布作业</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="settingWorkList" border>
                <el-table-column prop="id" label="作业ID" width="80" />
                <el-table-column prop="name" label="作业名称" min-width="150" show-overflow-tooltip />
                <el-table-column prop="courseId" label="课程号" width="80" />
                <el-table-column prop="courseName" label="课程名" min-width="120" show-overflow-tooltip />
                <el-table-column prop="notCompleteWorkNum" label="未完成人数" width="100" align="center">
                  <template slot-scope="scope">
                    {{ scope.row.notCompleteWorkNum !== undefined ? scope.row.notCompleteWorkNum : 0 }}
                  </template>
                </el-table-column>
                <el-table-column prop="completeWorkNum" label="已完成人数" width="100" align="center">
                  <template slot-scope="scope">
                    {{ scope.row.completeWorkNum !== undefined ? scope.row.completeWorkNum : 0 }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="350">
                  <template slot-scope="scope">
                    <el-button size="mini" @click="openEditWorkNameDialog(scope.row)">修改名称</el-button>
                    <el-button size="mini" type="danger" @click="deleteSettingWork(scope.row.id)">删除</el-button>
                    <el-button size="mini" type="primary" @click="viewWorkInStudent(scope.row)">查看学生作业</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination background layout="prev, pager, next" :current-page="settingWorkQuery.current"
                :page-size="workPage.pageSize" :total="settingWorkTotal"
                @current-change="val => { settingWorkQuery.current = val; loadSettingWorks() }">
              </el-pagination>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>

        <!-- 学分管理 -->
        <el-tab-pane label="学分管理" name="credits">
          <el-form :inline="true" :model="creditQuery">
            <el-form-item label="课程ID">
              <el-input v-model="creditQuery.courseId" placeholder="课程ID" clearable />
            </el-form-item>
            <el-form-item label="学生ID">
              <el-input v-model="creditQuery.studentId" placeholder="请输入学生ID" clearable />
            </el-form-item>
            <el-form-item label="课程状态">
              <el-select v-model="creditQuery.courseStatus" clearable placeholder="全部">
                <el-option label="进行中" :value="0" />
                <el-option label="批阅中" :value="1" />
                <el-option label="已完成" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchCredits">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="creditList" border>
            <el-table-column prop="studentId" label="学号" width="120" />
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="clazz" label="班级" width="120" />
            <el-table-column prop="courseId" label="课程id" width="80" />
            <el-table-column prop="courseName" label="课程名" min-width="120" />
            <el-table-column prop="credit" label="学分" width="80" align="center" />
            <el-table-column label="课程状态" width="100" align="center">
              <template slot-scope="scope">
                {{ ['进行中', '批阅中', '已完成'][scope.row.courseStatus] || '未知' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template slot-scope="scope">
                <el-button size="mini" @click="editCredit(scope.row)">修改学分</el-button>
                <el-button size="mini" type="danger" @click="dropCourse(scope.row)">退课</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination background layout="prev, pager, next" :current-page="creditQuery.current"
            :page-size="creditPage.pageSize" :total="creditTotal"
            @current-change="val => { creditQuery.current = val; loadCredits() }">
          </el-pagination>
        </el-tab-pane>

        <!-- 资源管理 -->
        <el-tab-pane label="资源管理" name="resources">
          <div class="stat-cards" v-if="resourceList.length > 0">
            <div class="stat-item">
              <span class="stat-label">课件：</span>
              <span class="stat-value">{{ typeCount[1] || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">试卷：</span>
              <span class="stat-value">{{ typeCount[2] || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">资料：</span>
              <span class="stat-value">{{ typeCount[3] || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">课本：</span>
              <span class="stat-value">{{ typeCount[4] || 0 }}</span>
            </div>
          </div>

          <el-form :inline="true" :model="resourceQuery">
            <el-form-item label="资源ID">
              <el-input v-model="resourceQuery.id" placeholder="资源ID" />
            </el-form-item>
            <el-form-item label="资源名称">
              <el-input v-model="resourceQuery.name" placeholder="资源名称" />
            </el-form-item>
            <el-form-item label="课程号">
              <el-input v-model="resourceQuery.courseId" placeholder="课程号" />
            </el-form-item>
            <el-form-item label="课程名">
              <el-input v-model="resourceQuery.courseName" placeholder="课程名" />
            </el-form-item>
            <el-form-item label="资源类型">
              <el-select v-model="resourceQuery.type" clearable placeholder="全部">
                <el-option label="课件" :value="1" />
                <el-option label="试卷" :value="2" />
                <el-option label="资料" :value="3" />
                <el-option label="课本" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchResources">查询</el-button>
              <el-button type="success" @click="showAddResourceDialog">上传资源</el-button>
            </el-form-item>
          </el-form>

          <div class="resource-card-container" v-loading="loadingResources">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in resourceList" :key="item.id">
                <el-card class="resource-card" shadow="hover">
                  <div v-if="item.picture" class="resource-card__image">
                    <img :src="getPictureUrl(item.picture)" :alt="item.name" @error="handleImageError" />
                  </div>
                  <div v-else class="resource-card__image resource-card__image--placeholder">
                    <i class="el-icon-files"></i>
                    <span>无封面</span>
                  </div>
                  <div class="resource-card__content">
                    <div class="resource-title" :title="item.name">{{ item.name }}</div>
                    <div class="resource-info">
                      <div><span class="label">资源ID：</span>{{ item.id }}</div>
                      <div><span class="label">教师ID：</span>{{ item.teacherId || '--' }}</div>
                      <div><span class="label">类型：</span>{{ getResourceTypeText(item.type) }}</div>
                      <div><span class="label">课程号：</span>{{ item.courseId }}</div>
                      <div><span class="label">课程名：</span>{{ item.courseName }}</div>
                      <div><span class="label">教师：</span>{{ item.teacherName }}</div>
                      <div><span class="label">更新时间：</span>{{ item.updateTime }}</div>
                      <div v-if="item.size !== undefined && item.size !== null">
                        <span class="label">大小：</span>{{ formatFileSize(item.size) }}
                      </div>
                      <div v-if="item.documentType">
                        <span class="label">格式：</span>{{ item.documentType.toUpperCase() }}
                      </div>
                      <div v-else-if="item.position">
                        <span class="label">格式：</span>{{ getFileExtension(item.position) }}
                      </div>
                    </div>
                    <div class="resource-actions">
                      <el-button size="small" type="primary" icon="el-icon-view" circle
                        @click="previewResource(item)"></el-button>
                      <el-button size="small" type="success" icon="el-icon-download" circle
                        @click="downloadResource(item.id)"></el-button>
                      <el-button size="small" type="default" icon="el-icon-edit" circle
                        @click="showEditResourceDialog(item)"></el-button>
                      <el-button size="small" type="danger" icon="el-icon-delete" circle
                        @click="deleteResource(item.id)"></el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <div v-if="resourceList.length === 0 && !loadingResources" class="empty-data">暂无资源数据</div>
          </div>
          <el-pagination class="resource-pagination" background layout="prev, pager, next"
            :current-page="resourceQuery.current" :page-size="resourcePageSize" :total="resourceTotal"
            @current-change="val => { resourceQuery.current = val; loadResources() }">
          </el-pagination>
        </el-tab-pane>
      </el-tabs>

      <!-- 课程对话框 -->
      <el-dialog :title="courseDialogTitle" :visible.sync="courseDialogVisible">
        <el-form :model="courseForm" label-width="100px">
          <el-form-item label="课程名">
            <el-input v-model="courseForm.name" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="courseForm.description" type="textarea" />
          </el-form-item>
          <el-form-item label="学期">
            <el-input v-model="courseForm.termPeriod" />
          </el-form-item>
          <el-form-item label="状态" v-if="courseForm.id">
            <el-select v-model="courseForm.courseStatus">
              <el-option label="进行中" :value="0" />
              <el-option label="批阅中" :value="1" />
              <el-option label="已完成" :value="2" />
            </el-select>
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="courseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCourse">保存</el-button>
        </span>
      </el-dialog>

      <!-- 发布作业对话框 -->
      <el-dialog title="发布作业" :visible.sync="publishWorkDialogVisible" width="30%">
        <el-form>
          <el-form-item label="课程号" required>
            <el-input v-model="publishCourseId" placeholder="请输入课程号" />
          </el-form-item>
          <el-form-item label="作业名称" required>
            <el-input v-model="newWorkName" placeholder="请输入作业名称" />
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="publishWorkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="publishWork">发布</el-button>
        </span>
      </el-dialog>

      <!-- 修改作业名称对话框 -->
      <el-dialog title="修改作业名称" :visible.sync="editWorkNameDialogVisible" width="30%">
        <el-form>
          <el-form-item label="作业名称" required>
            <el-input v-model="editingWorkName" placeholder="请输入新的作业名称" />
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="editWorkNameDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditWorkName">保存</el-button>
        </span>
      </el-dialog>

      <!-- 批阅作业对话框 -->
      <el-dialog title="批阅作业" :visible.sync="gradeWorkDialogVisible">
        <el-form>
          <el-form-item label="批阅状态" required>
            <el-select v-model="gradingWorkStatus" placeholder="请选择状态">
              <el-option label="已批阅" :value="1" />
              <el-option label="未批阅" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="分数" v-if="gradingWorkStatus === 1" required>
            <el-input v-model="gradingScore" placeholder="请输入分数（0-100）" />
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="gradeWorkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitGradeWork">提交</el-button>
        </span>
      </el-dialog>

      <!-- 修改学分对话框 -->
      <el-dialog title="修改学分" :visible.sync="creditDialogVisible">
        <el-form>
          <el-form-item label="新学分">
            <el-input v-model="editingCredit.credit" />
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="creditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitChangeCredit">提交</el-button>
        </span>
      </el-dialog>

      <!-- 上传资源对话框 -->
      <el-dialog title="上传资源" :visible.sync="addResourceDialogVisible" width="500px" @closed="resetResourceForm">
        <el-form :model="resourceForm" label-width="80px">
          <el-form-item label="资源名称" required>
            <el-input v-model="resourceForm.name" placeholder="请输入资源名称" />
          </el-form-item>
          <el-form-item label="课程号" required>
            <el-input v-model="resourceForm.courseId" placeholder="请输入所属课程号" />
          </el-form-item>
          <el-form-item label="资源类型" required>
            <el-select v-model="resourceForm.type" placeholder="请选择资源类型">
              <el-option label="课件" :value="1" />
              <el-option label="试卷" :value="2" />
              <el-option label="资料" :value="3" />
              <el-option label="课本" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="资源文件" required>
            <el-upload ref="fileUpload" action="#" :auto-upload="false" :on-change="handleFileChange"
              :on-remove="handleFileRemove" :limit="1" :file-list="resourceFileList">
              <el-button size="small" type="primary">选择文件</el-button>
              <div slot="tip" class="el-upload__tip">只能上传一个文件，且不超过50MB</div>
            </el-upload>
          </el-form-item>
          <el-form-item label="封面图片">
            <el-upload ref="pictureUpload" action="#" :auto-upload="false" :on-change="handlePictureChange"
              :on-remove="handlePictureRemove" :limit="1" :file-list="resourcePictureList">
              <el-button size="small" type="primary">选择图片</el-button>
              <div slot="tip" class="el-upload__tip">可选，建议上传封面图</div>
            </el-upload>
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="addResourceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddResource">上传</el-button>
        </span>
      </el-dialog>

      <!-- 编辑资源对话框 -->
      <el-dialog title="编辑资源" :visible.sync="editResourceDialogVisible" width="500px" @closed="resetEditResourceForm">
        <el-form :model="editResourceForm" label-width="80px">
          <el-form-item label="资源名称" required>
            <el-input v-model="editResourceForm.name" placeholder="请输入资源名称" />
          </el-form-item>
          <el-form-item label="课程号" required>
            <el-input v-model="editResourceForm.courseId" placeholder="请输入所属课程号" />
          </el-form-item>
          <el-form-item label="资源类型" required>
            <el-select v-model="editResourceForm.type" placeholder="请选择资源类型">
              <el-option label="课件" :value="1" />
              <el-option label="试卷" :value="2" />
              <el-option label="资料" :value="3" />
              <el-option label="课本" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="替换资源文件">
            <el-upload ref="editFileUpload" action="#" :auto-upload="false" :on-change="handleEditFileChange"
              :on-remove="handleEditFileRemove" :limit="1" :file-list="editResourceFileList">
              <el-button size="small" type="primary">选择文件</el-button>
              <div slot="tip" class="el-upload__tip">不选则保持原有文件</div>
            </el-upload>
          </el-form-item>
          <el-form-item label="替换封面图片">
            <el-upload ref="editPictureUpload" action="#" :auto-upload="false" :on-change="handleEditPictureChange"
              :on-remove="handleEditPictureRemove" :limit="1" :file-list="editResourcePictureList">
              <el-button size="small" type="primary">选择图片</el-button>
              <div slot="tip" class="el-upload__tip">不选则保持原有封面</div>
            </el-upload>
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="editResourceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditResource">保存修改</el-button>
        </span>
      </el-dialog>

      <!-- 资源预览弹窗 -->
      <el-dialog title="资源预览" :visible.sync="previewDialogVisible" width="80%" top="5vh" @close="closePreview"
        :close-on-click-modal="false" class="preview-dialog">
        <div v-loading="previewLoading" class="preview-container">
          <div v-if="previewType === 'image'" class="image-preview">
            <img :src="previewUrl" :alt="previewText" style="max-width: 100%; max-height: 70vh;" />
          </div>
          <div v-else-if="previewType === 'pdf'" class="pdf-preview">
            <iframe :src="previewUrl" width="100%" height="600px" frameborder="0"></iframe>
          </div>
          <div v-else-if="previewType === 'text'" class="text-preview">
            <pre>{{ textContent }}</pre>
          </div>
          <div v-else-if="previewType === 'unsupported'" class="unsupported-preview">
            <i class="el-icon-warning-outline"></i>
            <p>不支持预览此文件类型，请下载后查看</p>
            <el-button type="primary" @click="downloadCurrentPreview">下载文件</el-button>
          </div>
        </div>
        <span slot="footer">
          <el-button @click="previewDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="downloadCurrentPreview" v-if="previewType !== 'unsupported'">下载</el-button>
        </span>
      </el-dialog>

      <!-- AI批阅结果对话框 -->
      <el-dialog title="AI批阅结果" :visible.sync="aiDialogVisible" width="60%" :close-on-click-modal="false">
        <div v-loading="false" class="ai-result-container">
          <div v-if="aiAnalysisResult" class="ai-result-content">
            <div class="ai-result-header">
              <i class="el-icon-magic-stick"></i>
              <span>作业：{{ currentAnalysisWorkName }}</span>
            </div>
            <div class="ai-result-text">
              <pre>{{ aiAnalysisResult }}</pre>
            </div>
          </div>
          <div v-else class="ai-empty">
            暂无批阅结果
          </div>
        </div>
        <span slot="footer">
          <el-button @click="aiDialogVisible = false">关闭</el-button>
        </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import request from '@/utils/request'
import * as echarts from 'echarts'

export default {
  name: 'TeacherDashboard',
  data() {
    return {
      activeTab: 'statistics',
      // 课程管理
      courseQuery: { id: '', name: '', courseStatus: null, isAll: false, current: 1 },
      coursePage: { pageSize: 5 },
      courseTotal: 0,
      courseList: [],
      courseDialogVisible: false,
      courseDialogTitle: '新增课程',
      courseForm: { name: '', description: '', termPeriod: '', courseStatus: 0 },

      // 作业管理
      workSubTab: 'studentWorks',
      studentWorkQuery: { id: '', courseId: '', name: '', studentId: '', workStatus: 0, current: 1 },
      studentWorkList: [],
      studentWorkTotal: 0,
      settingWorkQuery: { id: '', courseId: '', name: '', current: 1 },
      settingWorkList: [],
      settingWorkTotal: 0,
      workPage: { pageSize: 5 },
      publishWorkDialogVisible: false,
      newWorkName: '',
      publishCourseId: '',
      editWorkNameDialogVisible: false,
      editingWorkId: null,
      editingWorkName: '',
      gradeWorkDialogVisible: false,
      gradingWorkId: null,
      gradingStudentId: null,
      gradingScore: '',
      gradingWorkStatus: 1,

      // 学分管理
      creditQuery: { courseId: '', studentId: '', courseStatus: null, current: 1 },
      creditPage: { pageSize: 5 },
      creditTotal: 0,
      creditList: [],
      creditDialogVisible: false,
      editingCredit: { courseId: null, studentId: null, credit: '' },

      // 资源管理
      resourceQuery: {
        current: 1,
        pageSize: 4,
        id: '',
        name: '',
        courseId: '',
        courseName: '',
        type: null
      },
      resourcePageSize: 4,
      resourceTotal: 0,
      resourceList: [],
      addResourceDialogVisible: false,
      resourceForm: { name: '', courseId: '', type: null },
      resourceFileList: [],
      resourcePictureList: [],
      selectedFile: null,
      selectedPicture: null,
      editResourceDialogVisible: false,
      editResourceForm: { id: null, name: '', courseId: '', type: null },
      editResourceFileList: [],
      editResourcePictureList: [],
      editSelectedFile: null,
      editSelectedPicture: null,
      loadingResources: false,

      // 预览相关
      previewDialogVisible: false,
      previewLoading: false,
      previewUrl: '',
      previewType: '',
      textContent: '',
      currentPreviewResource: null,

      // 统计模块
      statisticsLoading: false,
      statisticsData: {
        courseTotal: 0,
        workTotal: 0,
        resourceTotal: 0,
        studentTotal: 0,
        courseStatusDistribution: [
          { name: '已完成', value: 0 },
          { name: '批阅中', value: 0 },
          { name: '进行中', value: 0 }
        ],
        workStatusDistribution: [
          { name: '未批阅', value: 0 },
          { name: '已批阅', value: 0 }
        ],
        resourceTypeDistribution: [
          { name: '课件', value: 0 },
          { name: '试卷', value: 0 },
          { name: '资料', value: 0 },
          { name: '课本', value: 0 }
        ],
        scoreDistribution: [
          { range: '0-59', count: 0 },
          { range: '60-69', count: 0 },
          { range: '70-79', count: 0 },
          { range: '80-89', count: 0 },
          { range: '90-100', count: 0 }
        ]
      },
      charts: {
        courseStatus: null,
        workStatus: null,
        resourceType: null,
        scoreDistribution: null
      },

      // AI批阅
      aiLoadingMap: {},
      aiResultMap: {},
      aiDialogVisible: false,
      aiAnalysisResult: '',
      currentAnalysisWorkName: ''
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.userInfo || {}
    },
    teacherId() {
      return this.userInfo.teacherId
    },
    typeCount() {
      const counts = { 1: 0, 2: 0, 3: 0, 4: 0 }
      this.resourceList.forEach(item => {
        const typeNum = Number(item.type)
        if ([1, 2, 3, 4].includes(typeNum)) {
          counts[typeNum]++
        }
      })
      return counts
    },
    previewText() {
      return this.currentPreviewResource ? this.currentPreviewResource.name : '预览'
    }
  },
  created() {
    this.loadStatistics()
  },
  mounted() {
    window.addEventListener('resize', this.handleChartResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleChartResize)
    Object.values(this.charts).forEach(chart => {
      if (chart && typeof chart.dispose === 'function') {
        chart.dispose()
      }
    })
  },
  methods: {
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
    },

    parsePageResponse(res) {
      if (res && Array.isArray(res.data) && typeof res.total === 'number') {
        return { list: res.data, total: res.total }
      }
      if (res && res.data && Array.isArray(res.data.data) && typeof res.data.total === 'number') {
        return { list: res.data.data, total: res.data.total }
      }
      if (Array.isArray(res)) {
        return { list: res, total: res.length }
      }
      console.warn('未知的分页响应格式', res)
      return { list: [], total: 0 }
    },

    // ========== 课程管理 ==========
    searchCourses() {
      this.courseQuery.current = 1
      this.loadCourses()
    },
    async loadCourses() {
      try {
        const params = {
          current: this.courseQuery.current,
          pageSize: this.coursePage.pageSize
        }
        if (this.courseQuery.id) params.id = this.courseQuery.id
        if (this.courseQuery.name) params.name = this.courseQuery.name
        if (this.courseQuery.courseStatus !== null && this.courseQuery.courseStatus !== '') params.courseStatus = this.courseQuery.courseStatus
        params.isAll = this.courseQuery.isAll

        const res = await request.get('/teacher/course/find', { params })
        const { list, total } = this.parsePageResponse(res)
        this.courseList = list
        this.courseTotal = total
      } catch (e) {
        console.error('加载课程失败', e)
        this.$message.error('加载课程失败')
      }
    },
    showAddCourseDialog() {
      this.courseDialogTitle = '新增课程'
      this.courseForm = { name: '', description: '', termPeriod: '', courseStatus: 0 }
      this.courseDialogVisible = true
    },
    editCourse(row) {
      this.courseDialogTitle = '编辑课程'
      this.courseForm = {
        id: row.id,
        name: row.name,
        courseStatus: row.courseStatus,
        description: row.description,
        termPeriod: row.termPeriod
      }
      this.courseDialogVisible = true
    },
    async saveCourse() {
      try {
        const isAdd = !this.courseForm.id
        if (isAdd) {
          await request.post('/teacher/course/save', {
            name: this.courseForm.name,
            description: this.courseForm.description,
            termPeriod: this.courseForm.termPeriod,
            teacherId: this.teacherId
          })
          this.courseQuery.current = 1
        } else {
          await request.post('/teacher/course/update', this.courseForm)
        }
        this.$message.success('操作成功')
        this.courseDialogVisible = false
        this.loadCourses()
      } catch (e) {
        console.error('保存课程失败', e)
        this.$message.error('操作失败')
      }
    },
    async deleteCourse(id) {
      try {
        await this.$confirm('确认删除？', '提示', { type: 'warning' })
        await request.delete('/teacher/course/delete', { params: { id } })
        if (this.courseList.length === 1 && this.courseQuery.current > 1) {
          this.courseQuery.current -= 1
        }
        this.$message.success('删除成功')
        this.loadCourses()
      } catch (e) {
        if (e !== 'cancel') console.error('删除失败', e)
      }
    },

    // ========== 学生作业 ==========
    async loadStudentWorks() {
      try {
        const params = {
          current: this.studentWorkQuery.current,
          pageSize: this.workPage.pageSize,
          workStatus: this.studentWorkQuery.workStatus
        }
        if (this.studentWorkQuery.id) params.id = this.studentWorkQuery.id
        if (this.studentWorkQuery.courseId) params.courseId = this.studentWorkQuery.courseId
        if (this.studentWorkQuery.name) params.name = this.studentWorkQuery.name
        if (this.studentWorkQuery.studentId) params.studentId = this.studentWorkQuery.studentId

        const res = await request.get('/teacher/work/find', { params })
        const { list, total } = this.parsePageResponse(res)
        this.studentWorkList = list
        this.studentWorkTotal = total
      } catch (e) {
        console.error('加载学生作业失败', e)
        this.$message.error('加载学生作业失败')
      }
    },

    // ========== 作业设置 ==========
    async loadSettingWorks() {
      try {
        const params = {
          current: this.settingWorkQuery.current,
          pageSize: this.workPage.pageSize
        }
        if (this.settingWorkQuery.id) params.id = parseInt(this.settingWorkQuery.id)
        if (this.settingWorkQuery.courseId) params.courseId = parseInt(this.settingWorkQuery.courseId)
        if (this.settingWorkQuery.name) params.name = this.settingWorkQuery.name

        const res = await request.get('/teacher/work/search', { params })
        const { list, total } = this.parsePageResponse(res)
        this.settingWorkList = list
        this.settingWorkTotal = total
      } catch (e) {
        console.error('加载作业设置失败', e)
        this.$message.error('加载作业设置失败')
      }
    },
    searchSettingWorks() {
      this.settingWorkQuery.current = 1
      this.loadSettingWorks()
    },
    handleWorkSubTabClick() {
      if (this.workSubTab === 'studentWorks') {
        this.studentWorkQuery.current = 1
        this.loadStudentWorks()
      } else if (this.workSubTab === 'settingWorks') {
        this.settingWorkQuery.current = 1
        this.loadSettingWorks()
      }
    },

    showPublishWorkDialog() {
      this.newWorkName = ''
      if (this.workSubTab === 'settingWorks' && this.settingWorkQuery.courseId) {
        this.publishCourseId = this.settingWorkQuery.courseId
      } else {
        this.publishCourseId = ''
      }
      this.publishWorkDialogVisible = true
    },
    async publishWork() {
      if (!this.publishCourseId) {
        this.$message.warning('课程号不能为空')
        return
      }
      if (!this.newWorkName.trim()) {
        this.$message.warning('作业名称不能为空')
        return
      }
      try {
        await request.post('/teacher/work/set', null, {
          params: {
            name: this.newWorkName.trim(),
            courseId: this.publishCourseId
          }
        })
        this.$message.success('作业发布成功')
        this.publishWorkDialogVisible = false
        this.loadSettingWorks()
      } catch (error) {
        console.error('发布作业失败', error)
        this.$message.error('发布失败，请稍后重试')
      }
    },

    openEditWorkNameDialog(row) {
      this.editingWorkId = row.id
      this.editingWorkName = row.name
      this.editWorkNameDialogVisible = true
    },
    async submitEditWorkName() {
      if (!this.editingWorkName.trim()) {
        this.$message.warning('作业名称不能为空')
        return
      }
      try {
        await request.post('/teacher/work/update/name', {
          id: this.editingWorkId,
          name: this.editingWorkName.trim()
        })
        this.$message.success('作业名称修改成功')
        this.editWorkNameDialogVisible = false
        this.loadSettingWorks()
      } catch (e) {
        console.error('修改作业名称失败', e)
        this.$message.error('操作失败')
      }
    },

    async deleteSettingWork(workId) {
      try {
        await this.$confirm('确认删除该作业吗？', '提示', { type: 'warning' })
        await request.post('/teacher/work/delete', null, { params: { id: workId } })
        this.$message.success('删除成功')
        if (this.settingWorkList.length === 1 && this.settingWorkQuery.current > 1) {
          this.settingWorkQuery.current -= 1
        }
        this.loadSettingWorks()
      } catch (e) {
        if (e !== 'cancel') console.error('删除失败', e)
      }
    },

    viewWorkInStudent(row) {
      this.workSubTab = 'studentWorks'
      this.studentWorkQuery.courseId = row.courseId
      this.studentWorkQuery.name = row.name
      this.studentWorkQuery.id = ''
      this.studentWorkQuery.studentId = ''
      this.studentWorkQuery.workStatus = 0
      this.studentWorkQuery.current = 1
      this.loadStudentWorks()
    },

    openGradeDialog(row) {
      this.gradingWorkId = row.id
      this.gradingStudentId = row.studentId
      this.gradingScore = (row.score !== undefined && row.score !== null) ? row.score : ''
      this.gradingWorkStatus = 1
      this.gradeWorkDialogVisible = true
    },
    async submitGradeWork() {
      const requestBody = {
        id: this.gradingWorkId,
        studentId: this.gradingStudentId,
        workStatus: (this.gradingWorkStatus !== undefined && this.gradingWorkStatus !== null) ? this.gradingWorkStatus : 1
      }
      if (requestBody.workStatus === 1) {
        if (this.gradingScore === undefined || this.gradingScore === '') {
          this.$message.warning('分数不能为空')
          return
        }
        const scoreNum = Number(this.gradingScore)
        if (isNaN(scoreNum) || scoreNum < 0 || scoreNum > 100) {
          this.$message.warning('分数必须是 0-100 之间的数字')
          return
        }
        requestBody.score = scoreNum
      }
      try {
        await request.post('/teacher/work/update', requestBody)
        this.$message.success(requestBody.workStatus === 1 ? '批阅成功' : '已标记为未批阅')
        this.gradeWorkDialogVisible = false
        this.loadStudentWorks()
      } catch (e) {
        console.error('操作失败', e)
        this.$message.error('操作失败')
      }
    },

    async deleteStudentWork(workId) {
      try {
        await this.$confirm('确认删除该作业吗？', '提示', { type: 'warning' })
        await request.post('/teacher/work/delete', null, { params: { id: workId } })
        this.$message.success('删除成功')
        if (this.studentWorkList.length === 1 && this.studentWorkQuery.current > 1) {
          this.studentWorkQuery.current -= 1
        }
        this.loadStudentWorks()
      } catch (e) {
        if (e !== 'cancel') console.error('删除失败', e)
      }
    },

    // ========== 下载作业文件（严格对齐 /common/download 接口） ==========
    async downloadWork(row) {
      const workId = row.id;
      const studentId = row.studentId;
      
      if (!workId || !studentId) {
        this.$message.warning('作业ID或学生ID缺失，无法下载');
        return;
      }
      
      try {
        const res = await request.get('/common/download', {
          params: { workId, studentId },
          responseType: 'blob'
        });
        
        if (res.headers['content-type'] && res.headers['content-type'].includes('application/json')) {
          const text = await res.data.text();
          const error = JSON.parse(text);
          this.$message.error(error.message || '下载失败');
          return;
        }
        
        let fileName = null;
        const contentDisposition = res.headers['content-disposition'];
        if (contentDisposition) {
          const match = contentDisposition.match(/filename="?([^";]+)"?/);
          if (match && match[1]) {
            let raw = match[1];
            if (raw.startsWith('"') && raw.endsWith('"')) raw = raw.slice(1, -1);
            raw = raw.replace(/\+/g, '%20');
            fileName = decodeURIComponent(raw);
          }
        }
        
        if (!fileName) {
          const contentType = res.headers['content-type'] || '';
          let ext = '';
          if (contentType.includes('application/zip')) ext = '.zip';
          else if (contentType.includes('application/pdf')) ext = '.pdf';
          else if (contentType.includes('application/msword')) ext = '.doc';
          else if (contentType.includes('application/vnd.openxmlformats-officedocument.wordprocessingml.document')) ext = '.docx';
          else if (contentType.includes('text/plain')) ext = '.txt';
          else if (contentType.includes('image/')) ext = '.' + contentType.split('/')[1];
          fileName = `作业_${workId}_${studentId}${ext}`;
        }
        
        const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        this.$message.success('下载已开始');
      } catch (error) {
        console.error('下载作业失败', error);
        if (error.response && error.response.status === 404) {
          this.$message.error('作业文件不存在');
        } else {
          this.$message.error('下载失败，请稍后重试');
        }
      }
    },

    // ========== AI批阅（严格对齐 /ai/analysis 接口，传递 workId 和 studentId） ==========
    async callAiAnalysis(row) {
      const workId = row.id;
      const studentId = row.studentId;
      if (!workId || !studentId) {
        this.$message.warning('作业ID或学生ID缺失，无法进行AI批阅');
        return;
      }
      const key = workId; // 使用作业记录 ID 作为唯一标识
      if (this.aiLoadingMap[key]) return;
      this.$set(this.aiLoadingMap, key, true);
      try {
        const res = await request.get('/ai/analysis', {
          params: { workId, studentId },
          timeout: 600000
        });
        let resultText = '';
        if (typeof res === 'string') {
          resultText = res;
        } else if (res && typeof res === 'object') {
          // 后端返回 Result.success(data) ，data 是 AI 分析结果字符串
          resultText = res.data || res.msg || JSON.stringify(res, null, 2);
        } else {
          resultText = String(res);
        }
        const finalResult = resultText || 'AI批阅完成，但未返回具体内容';
        this.$set(this.aiResultMap, key, finalResult);
        this.$message.success(`作业“${row.name || workId}”AI批阅完成，可点击“查看结果”按钮查看。`);
      } catch (error) {
        console.error('AI批阅失败', error);
        const errorMsg = error.response?.data?.message || error.message || 'AI批阅服务异常';
        const errorResult = `批阅失败：${errorMsg}`;
        this.$set(this.aiResultMap, key, errorResult);
        this.$message.error(`作业“${row.name || workId}”AI批阅失败：${errorMsg}`);
      } finally {
        this.$set(this.aiLoadingMap, key, false);
      }
    },
    viewAiResult(workId, workName) {
      const result = this.aiResultMap[workId]
      if (!result) {
        this.$message.warning('暂无批阅结果，请先进行 AI 批阅')
        return
      }
      this.aiAnalysisResult = result
      this.currentAnalysisWorkName = workName || `作业${workId}`
      this.aiDialogVisible = true
    },

    // ========== 学分管理 ==========
    searchCredits() {
      this.creditQuery.current = 1
      this.loadCredits()
    },
    async loadCredits() {
      try {
        const params = {
          current: this.creditQuery.current,
          pageSize: this.creditPage.pageSize
        }
        if (this.creditQuery.courseId && this.creditQuery.courseId.trim()) {
          params.courseId = this.creditQuery.courseId.trim()
        }
        if (this.creditQuery.studentId && this.creditQuery.studentId.trim()) {
          params.studentId = this.creditQuery.studentId.trim()
        }
        if (this.creditQuery.courseStatus !== null && this.creditQuery.courseStatus !== '') {
          params.courseStatus = this.creditQuery.courseStatus
        }

        const res = await request.get('/teacher/credit/find', { params })
        const { list, total } = this.parsePageResponse(res)
        this.creditList = list
        this.creditTotal = total
      } catch (e) {
        console.error('加载学分失败', e)
        this.$message.error('加载学分失败')
      }
    },
    editCredit(row) {
      this.editingCredit = {
        courseId: row.courseId,
        studentId: row.studentId,
        credit: row.credit
      }
      this.creditDialogVisible = true
    },
    async submitChangeCredit() {
      try {
        await request.post('/teacher/credit/update', null, {
          params: {
            courseId: this.editingCredit.courseId,
            studentId: this.editingCredit.studentId,
            credit: this.editingCredit.credit
          }
        })
        this.$message.success('学分修改成功')
        this.creditDialogVisible = false
        this.loadCredits()
      } catch (e) {
        console.error('修改学分失败', e)
        this.$message.error('操作失败')
      }
    },
    async dropCourse(row) {
      if (!row.studentId || !row.courseId) {
        this.$message.warning('学号或课程ID缺失，无法退课')
        return
      }
      try {
        await this.$confirm(`确认将学生 ${row.name}（学号：${row.studentId}）从课程“${row.courseName}”中退课吗？`, '退课确认', {
          type: 'warning',
          confirmButtonText: '确认退课',
          cancelButtonText: '取消'
        })
        await request.post('/teacher/credit/delete', null, {
          params: {
            studentId: row.studentId,
            courseId: row.courseId
          }
        })
        this.$message.success('退课成功')
        if (this.creditList.length === 1 && this.creditQuery.current > 1) {
          this.creditQuery.current -= 1
        }
        this.loadCredits()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退课失败', error)
          this.$message.error('退课失败，请稍后重试')
        }
      }
    },

    // ========== 资源管理 ==========
    formatFileSize(bytes) {
      if (!bytes && bytes !== 0) return '--'
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    getFileExtension(filePath) {
      if (!filePath) return '--'
      const lastDot = filePath.lastIndexOf('.')
      if (lastDot === -1) return '--'
      return filePath.substring(lastDot + 1).toUpperCase()
    },
    getResourceTypeText(type) {
      const map = { 1: '课件', 2: '试卷', 3: '资料', 4: '课本' }
      return map[type] || '未知'
    },
    getPictureUrl(picture) {
      if (!picture) return '';
      const backendUrl = 'http://localhost:8080';
      let raw = String(picture).trim();
      if (raw.startsWith('http') || raw.startsWith('/images/')) {
        return raw;
      }
      let fileName = raw;
      if (raw.includes(':') || raw.includes('/') || raw.includes('\\')) {
        const parts = raw.split(/[\\/]/);
        fileName = parts[parts.length - 1];
      }
      fileName = encodeURIComponent(fileName);
      return `${backendUrl}/images/${fileName}`;
    },
    searchResources() {
      this.resourceQuery.current = 1
      this.loadResources()
    },
    async loadResources() {
      this.loadingResources = true
      try {
        const params = {
          current: this.resourceQuery.current,
          pageSize: this.resourcePageSize
        }
        if (this.resourceQuery.id) params.id = this.resourceQuery.id
        if (this.resourceQuery.name) params.name = this.resourceQuery.name
        if (this.resourceQuery.courseId) params.courseId = this.resourceQuery.courseId
        if (this.resourceQuery.courseName) params.courseName = this.resourceQuery.courseName
        if (this.resourceQuery.type !== null && this.resourceQuery.type !== undefined && this.resourceQuery.type !== '') {
          params.type = Number(this.resourceQuery.type)
        }

        const res = await request.get('/resource/find/teacher', { params })
        const { list, total } = this.parsePageResponse(res)
        this.resourceList = list
        this.resourceTotal = total
      } catch (e) {
        console.error('加载资源失败', e)
        this.$message.error('加载资源失败')
      } finally {
        this.loadingResources = false
      }
    },
    async downloadResource(id) {
      if (!id) {
        this.$message.warning('资源ID不存在')
        return
      }
      try {
        const res = await request.get('/resource/download', {
          params: { id },
          responseType: 'blob'
        })
        if (res.headers['content-type'] && res.headers['content-type'].includes('application/json')) {
          const text = await res.data.text()
          const error = JSON.parse(text)
          this.$message.error(error.message || '下载失败')
          return
        }
        let fileName = null
        const contentDisposition = res.headers['content-disposition']
        if (contentDisposition) {
          const match = contentDisposition.match(/filename="?([^";]+)"?/)
          if (match && match[1]) {
            let raw = match[1]
            if (raw.startsWith('"') && raw.endsWith('"')) raw = raw.slice(1, -1)
            raw = raw.replace(/\+/g, '%20')
            fileName = decodeURIComponent(raw)
          }
        }
        if (!fileName) {
          const contentType = res.headers['content-type'] || ''
          let ext = ''
          if (contentType.includes('application/zip')) ext = '.zip'
          else if (contentType.includes('application/pdf')) ext = '.pdf'
          else if (contentType.includes('application/msword')) ext = '.doc'
          else if (contentType.includes('application/vnd.openxmlformats-officedocument.wordprocessingml.document')) ext = '.docx'
          else if (contentType.includes('text/plain')) ext = '.txt'
          else if (contentType.includes('image/')) ext = '.' + contentType.split('/')[1]
          fileName = `resource_${id}${ext}`
        }
        const blob = new Blob([res.data], { type: res.headers['content-type'] || 'application/octet-stream' })
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)
        this.$message.success('下载已开始')
      } catch (error) {
        console.error('下载资源失败', error)
        if (error.response && error.response.status === 404) {
          this.$message.error('资源文件不存在')
        } else {
          this.$message.error('下载失败，请稍后重试')
        }
      }
    },
    async previewResource(resource) {
      if (!resource || !resource.id) {
        this.$message.warning('资源信息不完整')
        return
      }
      this.currentPreviewResource = resource
      this.previewDialogVisible = true
      this.previewLoading = true
      this.previewUrl = ''
      this.previewType = ''
      this.textContent = ''

      try {
        const res = await request.get('/resource/download', {
          params: { id: resource.id },
          responseType: 'blob'
        })
        if (res.headers['content-type'] && res.headers['content-type'].includes('application/json')) {
          const text = await res.data.text()
          const error = JSON.parse(text)
          this.$message.error(error.message || '获取文件失败')
          this.previewType = 'unsupported'
          this.previewLoading = false
          return
        }
        const contentType = res.headers['content-type'] || ''
        const blob = res.data
        const url = URL.createObjectURL(blob)

        if (contentType.startsWith('image/')) {
          this.previewType = 'image'
          this.previewUrl = url
        } else if (contentType === 'application/pdf') {
          this.previewType = 'pdf'
          this.previewUrl = url
        } else if (contentType.startsWith('text/') || contentType === 'application/json') {
          const text = await blob.text()
          this.previewType = 'text'
          this.textContent = text
          URL.revokeObjectURL(url)
        } else {
          this.previewType = 'unsupported'
          this.previewUrl = url
        }
      } catch (error) {
        console.error('预览失败', error)
        this.$message.error('预览失败，请稍后重试')
        this.previewType = 'unsupported'
      } finally {
        this.previewLoading = false
      }
    },
    closePreview() {
      if (this.previewUrl && (this.previewType === 'image' || this.previewType === 'pdf')) {
        URL.revokeObjectURL(this.previewUrl)
      }
      this.previewUrl = ''
      this.textContent = ''
      this.currentPreviewResource = null
    },
    downloadCurrentPreview() {
      if (this.currentPreviewResource) {
        this.downloadResource(this.currentPreviewResource.id)
      } else if (this.previewUrl && this.previewType !== 'unsupported') {
        const link = document.createElement('a')
        link.href = this.previewUrl
        link.download = this.currentPreviewResource?.name || 'resource'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      }
    },
    showAddResourceDialog() {
      this.resetResourceForm()
      this.addResourceDialogVisible = true
    },
    resetResourceForm() {
      this.resourceForm = { name: '', courseId: '', type: null }
      this.selectedFile = null
      this.selectedPicture = null
      this.resourceFileList = []
      this.resourcePictureList = []
      if (this.$refs.fileUpload) this.$refs.fileUpload.clearFiles()
      if (this.$refs.pictureUpload) this.$refs.pictureUpload.clearFiles()
    },
    handleFileChange(file, fileList) {
      this.selectedFile = file.raw
      this.resourceFileList = fileList.slice(-1)
    },
    handleFileRemove() {
      this.selectedFile = null
      this.resourceFileList = []
    },
    handlePictureChange(file, fileList) {
      this.selectedPicture = file.raw
      this.resourcePictureList = fileList.slice(-1)
    },
    handlePictureRemove() {
      this.selectedPicture = null
      this.resourcePictureList = []
    },
    async submitAddResource() {
      if (!this.resourceForm.name.trim()) {
        this.$message.warning('资源名称不能为空')
        return
      }
      if (!this.resourceForm.courseId) {
        this.$message.warning('课程号不能为空')
        return
      }
      if (this.resourceForm.type === null || this.resourceForm.type === '') {
        this.$message.warning('请选择资源类型')
        return
      }
      if (!this.selectedFile) {
        this.$message.warning('请选择要上传的资源文件')
        return
      }
      const formData = new FormData()
      formData.append('name', this.resourceForm.name.trim())
      formData.append('courseId', this.resourceForm.courseId)
      formData.append('type', this.resourceForm.type)
      formData.append('file', this.selectedFile)
      if (this.selectedPicture) {
        formData.append('picture', this.selectedPicture)
      }
      try {
        const res = await request.post('/resource/save', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.code === 0) {
          this.$message.success('资源上传成功')
          this.addResourceDialogVisible = false
          this.loadResources()
        } else {
          this.$message.error(res.msg || '上传失败')
        }
      } catch (e) {
        console.error('上传资源失败', e)
        this.$message.error('上传失败，请稍后重试')
      }
    },
    showEditResourceDialog(row) {
      this.editResourceForm = {
        id: row.id,
        name: row.name,
        courseId: row.courseId,
        type: row.type
      }
      this.editSelectedFile = null
      this.editSelectedPicture = null
      this.editResourceFileList = []
      this.editResourcePictureList = []
      if (this.$refs.editFileUpload) this.$refs.editFileUpload.clearFiles()
      if (this.$refs.editPictureUpload) this.$refs.editPictureUpload.clearFiles()
      this.editResourceDialogVisible = true
    },
    resetEditResourceForm() {
      this.editResourceForm = { id: null, name: '', courseId: '', type: null }
      this.editSelectedFile = null
      this.editSelectedPicture = null
      this.editResourceFileList = []
      this.editResourcePictureList = []
    },
    handleEditFileChange(file, fileList) {
      this.editSelectedFile = file.raw
      this.editResourceFileList = fileList.slice(-1)
    },
    handleEditFileRemove() {
      this.editSelectedFile = null
      this.editResourceFileList = []
    },
    handleEditPictureChange(file, fileList) {
      this.editSelectedPicture = file.raw
      this.editResourcePictureList = fileList.slice(-1)
    },
    handleEditPictureRemove() {
      this.editSelectedPicture = null
      this.editResourcePictureList = []
    },
    async submitEditResource() {
      if (!this.editResourceForm.name.trim()) {
        this.$message.warning('资源名称不能为空')
        return
      }
      if (!this.editResourceForm.courseId) {
        this.$message.warning('课程号不能为空')
        return
      }
      if (this.editResourceForm.type === null || this.editResourceForm.type === '') {
        this.$message.warning('请选择资源类型')
        return
      }
      const formData = new FormData()
      formData.append('id', this.editResourceForm.id)
      formData.append('name', this.editResourceForm.name.trim())
      formData.append('courseId', this.editResourceForm.courseId)
      formData.append('type', this.editResourceForm.type)
      if (this.editSelectedFile) {
        formData.append('file', this.editSelectedFile)
      }
      if (this.editSelectedPicture) {
        formData.append('picture', this.editSelectedPicture)
      }
      try {
        const res = await request.post('/resource/update', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.code === 0) {
          this.$message.success('资源修改成功')
          this.editResourceDialogVisible = false
          this.loadResources()
        } else {
          this.$message.error(res.msg || '修改失败')
        }
      } catch (e) {
        console.error('修改资源失败', e)
        this.$message.error('修改失败，请稍后重试')
      }
    },
    async deleteResource(id) {
      if (!id) return
      try {
        await this.$confirm('确认删除该资源吗？删除后无法恢复', '删除确认', { type: 'warning' })
        const res = await request.delete('/resource/delete', { params: { id } })
        if (res.code === 0) {
          this.$message.success('删除成功')
          if (this.resourceList.length === 1 && this.resourceQuery.current > 1) {
            this.resourceQuery.current -= 1
          }
          this.loadResources()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('删除资源失败', e)
          this.$message.error('删除失败')
        }
      }
    },
    handleImageError(e) {
      e.target.style.display = 'none'
      const parent = e.target.parentElement
      if (parent) {
        const placeholder = document.createElement('div')
        placeholder.className = 'image-placeholder'
        placeholder.innerHTML = '<i class="el-icon-picture-outline"></i><span>图片加载失败</span>'
        parent.appendChild(placeholder)
      }
    },

    // ========== 选项卡切换 ==========
    handleTabClick(tab) {
      if (tab.name === 'statistics') {
        this.loadStatistics()
      } else if (tab.name === 'courses') {
        this.loadCourses()
      } else if (tab.name === 'works') {
        if (this.workSubTab === 'studentWorks') {
          this.loadStudentWorks()
        } else {
          this.loadSettingWorks()
        }
      } else if (tab.name === 'credits') {
        this.loadCredits()
      } else if (tab.name === 'resources') {
        this.loadResources()
      }
    },
    viewCourseWorks(row) {
      this.workSubTab = 'settingWorks'
      this.settingWorkQuery.courseId = row.id
      this.settingWorkQuery.id = ''
      this.settingWorkQuery.name = ''
      this.settingWorkQuery.current = 1
      this.activeTab = 'works'
      this.loadSettingWorks()
    },

    // ========== 统计模块 ==========
    async loadStatistics() {
      this.statisticsLoading = true
      try {
        const res = await request.get('/common/information')
        const data = res && res.data ? res.data : res

        this.statisticsData.courseTotal = Number(data.courseTotal || 0)
        this.statisticsData.workTotal = Number(data.workTotal || 0)
        this.statisticsData.resourceTotal = Number(data.resourceTotal || 0)
        this.statisticsData.studentTotal = Number(data.studentTotal || 0)

        this.statisticsData.courseStatusDistribution = [
          { name: '已完成', value: Number(data.ccompleted || 0) },
          { name: '批阅中', value: Number(data.cinGrading || 0) },
          { name: '进行中', value: Number(data.cinProgress || 0) }
        ]

        this.statisticsData.workStatusDistribution = [
          { name: '未批阅', value: Number(data.wreviewing || 0) },
          { name: '已批阅', value: Number(data.wreviewed || 0) }
        ]

        this.statisticsData.resourceTypeDistribution = [
          { name: '课件', value: Number(data.courseWare || 0) },
          { name: '试卷', value: Number(data.examPaper || 0) },
          { name: '资料', value: Number(data.materials || 0) },
          { name: '课本', value: Number(data.textbook || 0) }
        ]

        this.statisticsData.scoreDistribution = [
          { range: '0-59', count: Number(data.w059 || 0) },
          { range: '60-69', count: Number(data.w6069 || 0) },
          { range: '70-79', count: Number(data.w7079 || 0) },
          { range: '80-89', count: Number(data.w8089 || 0) },
          { range: '90-100', count: Number(data.w90100 || 0) }
        ]

        await this.$nextTick()
        await new Promise(resolve => setTimeout(resolve, 80))
        this.renderAllCharts()
      } catch (error) {
        console.error('加载统计数据失败', error)
        this.$message.error('统计数据加载失败，请稍后重试')
      } finally {
        this.statisticsLoading = false
      }
    },
    renderAllCharts() {
      this.renderCourseStatusChart()
      this.renderWorkStatusChart()
      this.renderResourceTypeChart()
      this.renderScoreDistributionChart()
    },
    renderCourseStatusChart() {
      const container = this.$refs.courseStatusChart
      if (!container) return
      if (this.charts.courseStatus) {
        this.charts.courseStatus.dispose()
        this.charts.courseStatus = null
      }
      this.charts.courseStatus = echarts.init(container)
      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          type: 'pie',
          radius: '55%',
          data: this.statisticsData.courseStatusDistribution,
          label: { show: true, formatter: '{b}: {d}%' }
        }]
      }
      this.charts.courseStatus.setOption(option, true)
    },
    renderWorkStatusChart() {
      const container = this.$refs.workStatusChart
      if (!container) return
      if (this.charts.workStatus) {
        this.charts.workStatus.dispose()
        this.charts.workStatus = null
      }
      this.charts.workStatus = echarts.init(container)
      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          type: 'pie',
          radius: '55%',
          data: this.statisticsData.workStatusDistribution,
          label: { show: true, formatter: '{b}: {d}%' }
        }]
      }
      this.charts.workStatus.setOption(option, true)
    },
    renderResourceTypeChart() {
      const container = this.$refs.resourceTypeChart
      if (!container) return
      if (this.charts.resourceType) {
        this.charts.resourceType.dispose()
        this.charts.resourceType = null
      }
      this.charts.resourceType = echarts.init(container)
      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          type: 'pie',
          radius: '55%',
          data: this.statisticsData.resourceTypeDistribution,
          label: { show: true, formatter: '{b}: {d}%' }
        }]
      }
      this.charts.resourceType.setOption(option, true)
    },
    renderScoreDistributionChart() {
      const container = this.$refs.scoreDistributionChart
      if (!container) return
      if (this.charts.scoreDistribution) {
        this.charts.scoreDistribution.dispose()
        this.charts.scoreDistribution = null
      }
      this.charts.scoreDistribution = echarts.init(container)
      const xAxisData = this.statisticsData.scoreDistribution.map(item => item.range)
      const seriesData = this.statisticsData.scoreDistribution.map(item => item.count)
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        xAxis: { type: 'category', data: xAxisData, name: '分数段' },
        yAxis: { type: 'value', name: '作业数量' },
        series: [{
          type: 'bar',
          data: seriesData,
          itemStyle: { borderRadius: [4, 4, 0, 0], color: '#67C23A' }
        }]
      }
      this.charts.scoreDistribution.setOption(option, true)
    },
    handleChartResize() {
      Object.values(this.charts).forEach(chart => {
        if (chart && typeof chart.resize === 'function') {
          chart.resize()
        }
      })
    }
  }
}
</script>

<style scoped>
.el-header {
  background-color: #409EFF;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resource-card-container {
  margin-top: 20px;
  min-height: 400px;
}

.resource-card {
  margin-bottom: 20px;
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.resource-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.resource-card__image {
  height: 160px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.resource-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.resource-card__image--placeholder {
  flex-direction: column;
  color: #909399;
  font-size: 14px;
}

.resource-card__image--placeholder i {
  font-size: 48px;
  margin-bottom: 8px;
}

.resource-card__content {
  padding: 12px;
}

.resource-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resource-info {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
}

.resource-info div {
  line-height: 1.8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resource-info .label {
  color: #909399;
}

.resource-actions {
  display: flex;
  justify-content: space-around;
  gap: 8px;
  margin-top: 8px;
}

.empty-data {
  text-align: center;
  padding: 40px;
  color: #909399;
  font-size: 14px;
}

.resource-pagination {
  margin-top: 20px;
  text-align: right;
}

.stat-cards {
  display: flex;
  gap: 20px;
  background: #f0f9ff;
  padding: 12px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: baseline;
  gap: 4px;
  font-size: 14px;
}

.stat-label {
  color: #606266;
}

.stat-value {
  font-weight: bold;
  font-size: 18px;
  color: #409EFF;
}

.preview-container {
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-preview {
  text-align: center;
}

.pdf-preview {
  width: 100%;
}

.text-preview {
  width: 100%;
  max-height: 70vh;
  overflow: auto;
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
}

.text-preview pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: monospace;
  font-size: 14px;
}

.unsupported-preview {
  text-align: center;
  padding: 40px;
}

.unsupported-preview i {
  font-size: 64px;
  color: #e6a23c;
  margin-bottom: 16px;
}

.unsupported-preview p {
  margin-bottom: 20px;
  color: #606266;
}

.statistics-container {
  padding: 10px 0;
}

.stat-cards-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 16px;
  display: flex;
  align-items: center;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.stat-card-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-card-icon i {
  font-size: 28px;
  color: #409EFF;
}

.stat-card-content {
  flex: 1;
}

.stat-card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-card-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.chart-row {
  margin-bottom: 24px;
}

.chart-box {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 16px;
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
}

.ai-result-container {
  min-height: 200px;
}

.ai-result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
  font-size: 16px;
  font-weight: 500;
  color: #409EFF;
}

.ai-result-header i {
  font-size: 20px;
}

.ai-result-text {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
}

.ai-result-text pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #2c3e50;
}

.ai-empty {
  text-align: center;
  padding: 40px;
  color: #909399;
}
</style>