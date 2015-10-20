//取出数组最后一个元素
Array.prototype.last = function() {
    return this[this.length - 1]
}

//取出数组第一个元素
Array.prototype.first = function() {
    return this[0]
}


//文件名去掉后缀
String.prototype.trimSuf = function() {
    return this.split('.').first();
}

//重写string 的 trim 函数
String.prototype.trim = function(str) {
    if (!str) {
        str = ' ';
    }
    var lt = str.length;
    if (this.length > lt && this.lastIndexOf(str) == this.length - lt) {
        var l = this.lastIndexOf(str);
        var that = this.substring(0, l);
        return that.trim(str);
    }
    return Array.prototype.join.apply(this, ['']);
}