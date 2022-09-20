class Queue {
    constructor() {
        this._queue = [];
    }

    push(value) {
        return this._queue.push(value);
    }

    shift() {
        return this._queue.shift();
    }

    isEmpty() {
        return this._queue.length === 0;
    }
}

class DelayedTask {
    constructor(resolve, fn) {
        this.resolve = resolve;
        this.fn = fn;
    }
}

export default class TaskPool {
    constructor(size) {
        this.size = size;
        this.queue = new Queue();
    }

    addTask(fn) {
        return new Promise((resolve) => {
            this.queue.push(new DelayedTask(resolve, fn));
            if (this.size) {
                this.size--;
                const {resolve: taskResole, fn} = this.queue.shift();
                taskResole(this.runTask(fn));
            }
        })
    }

    pullTask() {
        if (this.queue.isEmpty()) {
            return;
        }

        if (this.size === 0) {
            return;
        }

        this.size++;
        const {resolve, fn} = this.queue.shift();
        resolve(this.runTask(fn));
    }

    runTask(fn) {
        const result = Promise.resolve(fn());

        result.then(() => {
            this.size--;
            this.pullTask();
        }).catch(() => {
            this.size--;
            this.pullTask();
        })

        return result;
    }
}