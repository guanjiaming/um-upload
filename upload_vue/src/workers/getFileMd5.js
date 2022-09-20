import SparkMD5 from "spark-md5"
self.addEventListener("message", async function (event) {
    getFileMd5(event.data);
})

function getFileMd5(file) {
    console.log("开始时间", Date.now());
    let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
        chunkSize = 2097152,                             // Read in chunks of 2MB
        chunks = Math.ceil(file.size / chunkSize),
        currentChunk = 0,
        spark = new SparkMD5.ArrayBuffer(),
        fileReader = new FileReader();

    loadNext();

    function loadNext() {
        let start = currentChunk * chunkSize,
            end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;

        fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    }

    fileReader.onload = function (e) {
        console.log('read chunk nr', currentChunk + 1, 'of', chunks);  // Append array buffer
        spark.append(e.target.result);
        currentChunk++;

        if (currentChunk < chunks) {
            loadNext();
        } else {
            console.log('finished loading');
            console.log("结束时间", Date.now());
            self.postMessage(spark.end());
            self.close();
        }
    };

    fileReader.onerror = function () {
        self.postMessage("");
        console.warn('oops, something went wrong.');
        self.close();
    };
}