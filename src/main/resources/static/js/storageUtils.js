// storageUtils.js - 本地存储工具函数

(function(global) {
    'use strict';

    const StorageUtil = {
        /**
         * 设置存储项
         * @param {string} key - 键名
         * @param {string} value - 值
         */
        set: function(key, value) {
            try {
                localStorage.setItem(key, value);
                console.log('存储成功:', key, '=', value.substring(0, 20) + '...');
                return true;
            } catch (e) {
                console.error('存储失败:', e);
                return false;
            }
        },

        /**
         * 获取存储项
         * @param {string} key - 键名
         * @returns {string|null} 存储的值或null
         */
        get: function(key) {
            try {
                const value = localStorage.getItem(key);
                if (value) {
                    console.log('获取存储:', key, '=', value.substring(0, 20) + '...');
                } else {
                    console.log('存储项不存在:', key);
                }
                return value;
            } catch (e) {
                console.error('获取存储失败:', e);
                return null;
            }
        },

        /**
         * 删除存储项
         * @param {string} key - 键名
         */
        remove: function(key) {
            try {
                localStorage.removeItem(key);
                console.log('删除存储:', key);
            } catch (e) {
                console.error('删除存储失败:', e);
            }
        },

        /**
         * 检查存储项是否存在
         * @param {string} key - 键名
         * @returns {boolean} 是否存在
         */
        exists: function(key) {
            return this.get(key) !== null;
        },

        /**
         * 清空所有存储
         */
        clear: function() {
            try {
                localStorage.clear();
                console.log('清空所有存储');
            } catch (e) {
                console.error('清空存储失败:', e);
            }
        },

        /**
         * 设置JWT Token
         * @param {string} token - JWT Token
         */
        setToken: function(token) {
            return this.set('jwt_token', token);
        },

        /**
         * 获取JWT Token
         * @returns {string|null} JWT Token
         */
        getToken: function() {
            return this.get('jwt_token');
        },

        /**
         * 删除JWT Token
         */
        removeToken: function() {
            this.remove('jwt_token');
        },

        /**
         * 检查是否已登录
         * @returns {boolean} 是否已登录
         */
        isLoggedIn: function() {
            return this.exists('jwt_token');
        },

        /**
         * 设置AJAX请求的Authorization头
         * @param {XMLHttpRequest} xhr - XMLHttpRequest对象
         */
        setAuthHeader: function(xhr) {
            const token = this.getToken();
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                console.log('已设置Authorization头');
            } else {
                console.warn('未找到JWT Token');
            }
        }
    };

    // 将StorageUtil暴露到全局作用域
    global.StorageUtil = StorageUtil;

    // 如果jQuery存在，设置全局AJAX请求头
    if (typeof jQuery !== 'undefined') {
        jQuery.ajaxSetup({
            beforeSend: function(xhr) {
                StorageUtil.setAuthHeader(xhr);
            }
        });
    }

})(window);