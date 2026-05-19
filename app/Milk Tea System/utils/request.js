const BASE_URL = 'http://localhost:8080/milkteasystem'

// 过滤掉 null / undefined 参数，避免序列化成 "null" 字符串传给后端
const cleanData = (data) => {
  if (!data) return data
  const cleaned = {}
  for (const key of Object.keys(data)) {
    if (data[key] !== null && data[key] !== undefined) {
      cleaned[key] = data[key]
    }
  }
  return cleaned
}

const request = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('satoken')
    uni.request({
      url: BASE_URL + url,
      method: options.method || 'GET',
      data: cleanData(options.data),
      header: {
        'Content-Type': 'application/json',
        'satoken': token || ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const body = res.data
          if (body.code === 200) {
            resolve(body)
          } else {
            uni.showToast({ title: body.msg || '请求失败', icon: 'none' })
            reject(body)
          }
        } else if (res.statusCode === 401) {
          uni.removeStorageSync('satoken')
          uni.removeStorageSync('userInfo')
          uni.showToast({ title: '请先登录', icon: 'none' })
          reject(res)
        } else {
          uni.showToast({ title: '服务器错误', icon: 'none' })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常，请检查连接', icon: 'none' })
        reject(err)
      }
    })
  })
}

request.get = (url, data) => request(url, { method: 'GET', data })
request.post = (url, data) => request(url, { method: 'POST', data })
request.put = (url, data) => request(url, { method: 'PUT', data })
request.delete = (url, data) => request(url, { method: 'DELETE', data })

export default request
