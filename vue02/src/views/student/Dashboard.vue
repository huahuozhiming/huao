<template>
  <el-container>
    <el-header>
      <h3>学生端</h3>
      <div>
        <span>{{ userInfo.name }} ({{ userInfo.id }})</span>
        <el-button @click="logout">退出</el-button>
      </div>
    </el-header>
    <el-main>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 课程管理标签页 -->
        <el-tab-pane label="课程管理" name="courses">
          <el-form :inline="true" :model="courseQuery">
            <el-form-item label="课程号">
              <el-input v-model="courseQuery.id" placeholder="输入课程号筛选" clearable />
            </el-form-item>
            <el-form-item label="课程名">
              <el-input v-model="courseQuery.name" placeholder="输入名称筛选" clearable />
            </el-form-item>
            <el-form-item label="课程状态">
              <el-select v-model="courseQuery.courseStatus" clearable placeholder="全部">
                <el-option label="进行中" :value="0" />
                <el-option label="批阅中" :value="1" />
                <el-option label="已完成" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadCourses">查询</el-button>
            </el-form-item>
            <el-form-item style="margin-left: 20px">
              <el-radio-group v-model="isAllCourses" @change="onCourseModeChange">
                <el-radio :label="false">我的课程</el-radio>
                <el-radio :label="true">所有课程</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <el-table :data="courseList" border>
            <el-table-column prop="id" label="课程号" />
            <el-table-column prop="name" label="课程名" />
            <el-table-column prop="teacherName" label="授课教师" />
            <el-table-column prop="termPeriod" label="学期" />
            <el-table-column label="状态">
              <template slot-scope="scope">
                {{ ['进行中','批阅中','已完成'][scope.row.courseStatus] || '未知' }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="课程描述" />
            <el-table-column v-if="isAllCourses" prop="studentTotal" label="选课人数" />
            <el-table-column v-if="!isAllCourses" prop="credit" label="学分" />
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                  v-if="isAllCourses"
                  size="mini"
                  type="primary"
                  @click="selectCourse(scope.row.id)"
                >选课</el-button>
                <template v-else>
                  <el-button
                    size="mini"
                    type="danger"
                    @click="dropCourse(scope.row.id)"
                  >退课</el-button>
                  <el-button
                    size="mini"
                    type="primary"
                    @click="gotoWorks(scope.row.id)"
                  >作业</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            background
            layout="prev, pager, next"
            :current-page="courseQuery.current"
            :page-size="courseQuery.pageSize"
            :total="courseTotal"
            @current-change="val => { courseQuery.current = val; loadCourses() }">
          </el-pagination>
        </el-tab-pane>

        <!-- 作业管理标签页 -->
        <el-tab-pane label="作业管理" name="works">
          <el-tabs v-model="workMainTab" @tab-click="handleWorkMainTabClick">
            <!-- 作业任务 -->
            <el-tab-pane label="作业任务" name="tasks">
              <el-form :inline="true" :model="taskQuery">
                <el-form-item label="作业ID">
                  <el-input v-model="taskQuery.id" placeholder="作业ID" clearable />
                </el-form-item>
                <el-form-item label="作业名">
                  <el-input v-model="taskQuery.name" placeholder="作业名" clearable />
                </el-form-item>
                <el-form-item label="课程号">
                  <el-input v-model="taskQuery.courseId" placeholder="课程号" clearable />
                </el-form-item>
                <el-form-item label="课程名">
                  <el-input v-model="taskQuery.courseName" placeholder="课程名" clearable />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="searchWorkTasks">查询</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="taskList" border>
                <el-table-column prop="id" label="作业ID" />
                <el-table-column prop="name" label="作业名" />
                <el-table-column prop="courseId" label="课程号" />
                <el-table-column prop="courseName" label="课程名" />
                <el-table-column prop="teacherName" label="授课教师" />
                <el-table-column label="操作" width="180">
                  <template slot-scope="scope">
                    <el-button size="mini" type="primary" @click="submitTaskWork(scope.row)">提交作业</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                background
                layout="prev, pager, next"
                :current-page="taskQuery.current"
                :page-size="workPageSize"
                :total="taskTotal"
                @current-change="val => { taskQuery.current = val; loadWorkTasks() }">
              </el-pagination>
            </el-tab-pane>

            <!-- 我的作业（已修改下载按钮） -->
            <el-tab-pane label="我的作业" name="myWorks">
              <el-form :inline="true" :model="myWorkQuery">
                <el-form-item label="作业ID">
                  <el-input v-model="myWorkQuery.id" placeholder="作业ID" clearable />
                </el-form-item>
                <el-form-item label="作业名">
                  <el-input v-model="myWorkQuery.name" placeholder="作业名" clearable />
                </el-form-item>
                <el-form-item label="课程号">
                  <el-input v-model="myWorkQuery.courseId" placeholder="课程号" clearable />
                </el-form-item>
                <el-form-item label="课程名">
                  <el-input v-model="myWorkQuery.courseName" placeholder="课程名" clearable />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="myWorkQuery.workStatus" placeholder="请选择状态" clearable>
                    <el-option label="未批阅" :value="0" />
                    <el-option label="已批阅" :value="1" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="searchMyWorks">查询</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="myWorkList" border>
                <el-table-column prop="id" label="作业ID" />
                <el-table-column prop="name" label="作业名" />
                <el-table-column prop="courseId" label="课程号" />
                <el-table-column prop="courseName" label="课程名" />
                <el-table-column prop="teacherName" label="授课教师" />
                <el-table-column label="状态">
                  <template slot-scope="scope">
                    <span>{{ scope.row.workStatus === 0 ? '未批阅' : '已批阅' }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="score" label="分数" />
                <el-table-column label="操作" width="180">
                  <template slot-scope="scope">
                    <el-button size="mini" @click="openChangeDialog(scope.row)">修改提交</el-button>
                    <!-- 修改1：下载按钮传递整行对象，以便提取 workId 和 studentId -->
                    <el-button size="mini" type="success" @click="downloadWork(scope.row)">下载</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                background
                layout="prev, pager, next"
                :current-page="myWorkQuery.current"
                :page-size="workPageSize"
                :total="myWorkTotal"
                @current-change="val => { myWorkQuery.current = val; loadMyWorks() }">
              </el-pagination>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>

        <!-- 资源管理标签页 - 适配 /resource/find/student -->
        <el-tab-pane label="资源管理" name="resources">
          <!-- 顶部统计卡片 -->
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
              <el-input v-model="resourceQuery.id" placeholder="资源ID" clearable />
            </el-form-item>
            <el-form-item label="资源名称">
              <el-input v-model="resourceQuery.name" placeholder="资源名称" clearable />
            </el-form-item>
            <el-form-item label="课程号">
              <el-input v-model="resourceQuery.courseId" placeholder="课程号" clearable />
            </el-form-item>
            <el-form-item label="课程名">
              <el-input v-model="resourceQuery.courseName" placeholder="课程名" clearable />
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
            </el-form-item>
          </el-form>

          <!-- 卡片列表 -->
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

      <!-- 修改作业对话框（适配 /student/work/change 接口） -->
      <el-dialog title="修改作业" :visible.sync="changeDialogVisible" width="400px">
        <el-form>
          <el-form-item label="新文件">
            <input type="file" ref="changeFile" />
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="changeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitChange">确认修改</el-button>
        </span>
      </el-dialog>

      <!-- 上传新作业对话框（适配 /student/work/save 接口） -->
      <el-dialog title="提交作业" :visible.sync="newWorkDialogVisible" width="450px">
        <el-form label-width="80px">
          <el-form-item label="作业ID">
            <el-input v-model="newWorkForm.workId" disabled />
          </el-form-item>
          <el-form-item label="课程号">
            <el-input v-model="newWorkForm.courseId" disabled />
          </el-form-item>
          <el-form-item label="选择文件">
            <input type="file" ref="newWorkFile" />
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="newWorkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNewWork">提交</el-button>
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
    </el-main>
  </el-container>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'StudentDashboard',
  data() {
    return {
      activeTab: 'courses',
      // 课程管理
      courseQuery: {
        id: '',
        name: '',
        courseStatus: null,
        current: 1,
        pageSize: 5
      },
      courseTotal: 0,
      courseList: [],
      isAllCourses: false,
      
      // 作业管理
      workMainTab: 'tasks',
      taskQuery: { 
        id: '',
        name: '',
        courseId: '',
        courseName: '',
        current: 1
      },
      taskList: [],
      taskTotal: 0,
      // 我的作业查询
      myWorkQuery: {
        id: '',
        name: '',
        courseId: '',
        courseName: '',
        workStatus: null,
        current: 1
      },
      myWorkList: [],
      myWorkTotal: 0,
      workPageSize: 5,
      changeDialogVisible: false,
      currentWorkId: null,
      newWorkDialogVisible: false,
      newWorkForm: { courseId: '', workId: null },

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
      loadingResources: false,

      // 预览相关
      previewDialogVisible: false,
      previewLoading: false,
      previewUrl: '',
      previewType: '',
      textContent: '',
      currentPreviewResource: null
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.userInfo || {}
    },
    studentId() {
      // 获取当前登录学生的ID
      return this.userInfo.id
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
    this.loadCourses()
  },
  methods: {
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
    },
    onCourseModeChange() {
      this.courseQuery.current = 1
      this.loadCourses()
    },
    async loadCourses() {
      try {
        const params = {
          id: this.courseQuery.id || undefined,
          name: this.courseQuery.name || undefined,
          current: this.courseQuery.current,
          pageSize: this.courseQuery.pageSize,
          isAll: this.isAllCourses,
          courseStatus: this.courseQuery.courseStatus !== null ? this.courseQuery.courseStatus : undefined
        }
        const res = await request.get('/student/course/find', { params })
        this.courseList = res.data.data || []
        this.courseTotal = res.data.total || 0
      } catch (e) {
        console.error('加载课程失败', e)
      }
    },
    async dropCourse(courseId) {
      try {
        await this.$confirm('确认退课？', '提示', { type: 'warning' })
        await request.get('/student/course/delete', { params: { courseId } })
        this.$message.success('退课成功')
        this.loadCourses()
      } catch (e) {
        if (e !== 'cancel') console.error(e)
      }
    },
    async selectCourse(courseId) {
      try {
        await this.$confirm('确认选修该课程？', '提示', { type: 'info' })
        await request.get('/student/course/save', { params: { courseId } })
        this.$message.success('选课成功')
        this.loadCourses()
      } catch (e) {
        if (e !== 'cancel') console.error(e)
      }
    },
    gotoWorks(courseId) {
      this.activeTab = 'works'
      this.workMainTab = 'tasks'
      this.taskQuery.courseId = String(courseId)
      this.taskQuery.current = 1
      this.loadWorkTasks()
    },

    // ================== 作业任务 ==================
    searchWorkTasks() {
      this.taskQuery.current = 1
      this.loadWorkTasks()
    },
    async loadWorkTasks() {
      try {
        const params = {
          current: this.taskQuery.current,
          pageSize: this.workPageSize,
          id: this.taskQuery.id || undefined,
          name: this.taskQuery.name || undefined,
          courseId: this.taskQuery.courseId || undefined,
          courseName: this.taskQuery.courseName || undefined
        }
        const res = await request.get('/student/work/search', { params })
        this.taskList = res.data.data || []
        this.taskTotal = res.data.total || 0
      } catch (e) {
        console.error('加载作业任务失败', e)
      }
    },
    submitTaskWork(task) {
      this.newWorkForm.workId = task.id
      this.newWorkForm.courseId = task.courseId
      this.newWorkDialogVisible = true
    },

    // ================== 我的作业 ==================
    searchMyWorks() {
      this.myWorkQuery.current = 1
      this.loadMyWorks()
    },
    async loadMyWorks() {
      try {
        const params = {
          current: this.myWorkQuery.current,
          pageSize: this.workPageSize
        }
        if (this.myWorkQuery.id) params.id = Number(this.myWorkQuery.id)
        if (this.myWorkQuery.name) params.name = this.myWorkQuery.name
        if (this.myWorkQuery.courseId) params.courseId = Number(this.myWorkQuery.courseId)
        if (this.myWorkQuery.courseName) params.courseName = this.myWorkQuery.courseName
        if (this.myWorkQuery.workStatus !== null && this.myWorkQuery.workStatus !== '') {
          params.workStatus = Number(this.myWorkQuery.workStatus)
        }

        const res = await request.get('/student/work/find', { params })
        this.myWorkList = res.data.data || []
        this.myWorkTotal = res.data.total || 0
      } catch (e) {
        console.error('加载我的作业失败', e)
        this.$message.error('加载作业失败')
      }
    },
    handleWorkMainTabClick(tab) {
      if (tab.name === 'tasks') {
        this.taskQuery.current = 1
        this.loadWorkTasks()
      } else if (tab.name === 'myWorks') {
        this.myWorkQuery.current = 1
        this.loadMyWorks()
      }
    },

    /**
     * 修改后的下载作业方法（严格对齐接口 /common/download?workId=&studentId=）
     * @param {Object} row - 当前行数据，需包含 id（作业记录ID）
     */
    async downloadWork(row) {
      if (!row || !row.id) {
        this.$message.warning('作业ID缺失')
        return
      }
      const workId = row.id;
      // 学生ID从当前登录用户获取
      const studentId = this.studentId;
      if (!studentId) {
        this.$message.warning('学生ID缺失，请重新登录')
        return
      }

      try {
        const res = await request.get('/common/download', {
          params: { workId, studentId },
          responseType: 'blob'
        });

        // 判断是否为错误 JSON 响应
        if (res.headers['content-type'] && res.headers['content-type'].includes('application/json')) {
          const text = await res.data.text();
          const error = JSON.parse(text);
          this.$message.error(error.message || '下载失败');
          return;
        }

        // 提取文件名
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

        // 若无文件名，根据 Content-Type 生成默认名
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

    openChangeDialog(row) {
      if (!row.id) {
        this.$message.warning('作业ID缺失')
        return
      }
      this.currentWorkId = row.id
      this.changeDialogVisible = true
    },
    async submitChange() {
      const file = this.$refs.changeFile.files[0]
      if (!file) {
        this.$message.warning('请选择新文件')
        return
      }
      const formData = new FormData()
      formData.append('file', file)
      formData.append('workId', this.currentWorkId)
      try {
        await request.post('/student/work/change', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        this.$message.success('修改成功')
        this.changeDialogVisible = false
        if (this.workMainTab === 'myWorks') {
          this.loadMyWorks()
        }
      } catch (e) {
        console.error(e)
        this.$message.error('修改失败')
      }
    },
    async submitNewWork() {
      const file = this.$refs.newWorkFile.files[0]
      if (!file) {
        this.$message.warning('请选择文件')
        return
      }
      if (!this.newWorkForm.workId) {
        this.$message.warning('作业ID缺失')
        return
      }
      if (!this.newWorkForm.courseId) {
        this.$message.warning('课程号缺失')
        return
      }
      const formData = new FormData()
      formData.append('file', file)
      formData.append('workId', this.newWorkForm.workId)
      formData.append('courseId', this.newWorkForm.courseId)
      try {
        await request.post('/student/work/save', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        this.$message.success('作业上传成功')
        this.newWorkDialogVisible = false
        this.workMainTab = 'myWorks'
        this.myWorkQuery.workStatus = null
        this.myWorkQuery.current = 1
        this.loadMyWorks()
      } catch (e) {
        console.error(e)
        this.$message.error('上传失败')
      }
    },

    // ==================== 资源管理 ====================
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
      if (!picture) return ''
      const backendUrl = process.env.VUE_APP_BASE_API || 'http://localhost:8080'
      let raw = String(picture).trim()
      if (raw.startsWith('http') || raw.startsWith('/images/')) {
        return raw
      }
      let fileName = raw
      if (raw.includes(':') || raw.includes('/') || raw.includes('\\')) {
        const parts = raw.split(/[\\/]/)
        fileName = parts[parts.length - 1]
      }
      fileName = encodeURIComponent(fileName)
      return `${backendUrl}/images/${fileName}`
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
        if (this.resourceQuery.id) params.id = Number(this.resourceQuery.id)
        if (this.resourceQuery.name) params.name = this.resourceQuery.name
        if (this.resourceQuery.courseId) params.courseId = Number(this.resourceQuery.courseId)
        if (this.resourceQuery.courseName) params.courseName = this.resourceQuery.courseName
        if (this.resourceQuery.type !== null && this.resourceQuery.type !== undefined && this.resourceQuery.type !== '') {
          params.type = Number(this.resourceQuery.type)
        }

        const res = await request.get('/resource/find/student', { params })

        let list = [], total = 0
        if (res && Array.isArray(res.data) && typeof res.total === 'number') {
          list = res.data
          total = res.total
        } else if (res && res.data && Array.isArray(res.data.data) && typeof res.data.total === 'number') {
          list = res.data.data
          total = res.data.total
        } else if (Array.isArray(res)) {
          list = res
          total = res.length
        } else {
          console.warn('未知的资源返回格式', res)
        }

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

    handleTabClick(tab) {
      if (tab.name === 'courses') {
        this.loadCourses()
      } else if (tab.name === 'works') {
        if (this.workMainTab === 'tasks') {
          this.loadWorkTasks()
        } else if (this.workMainTab === 'myWorks') {
          this.loadMyWorks()
        }
      } else if (tab.name === 'resources') {
        this.loadResources()
      }
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
</style>