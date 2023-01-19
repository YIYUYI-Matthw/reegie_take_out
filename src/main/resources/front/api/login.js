function loginApi(data) {
    return $axios({
        'url': '/user/login',
        'method': 'post',
        data
    })
}

function loginoutApi() {
    return $axios({
        'url': '/user/loginout',
        'method': 'post',
    })
}

function requestCodeApi(data) {
    // 这里的data包括手机号和验证码
    return $axios({
        'url': '/user/getCode',
        'method': 'get',
        params: data
    })
}

  