import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        steady: {
            executor: 'constant-arrival-rate',
            rate: 1500,
            timeUnit: '1s',
            duration: '60s',
            preAllocatedVUs: 400,
            maxVUs: 800,
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        // под sleep=200ms поставьте разумно, например:
        http_req_duration: ['p(95)<600'],
    },
};

export default function () {
    const res = http.get('http://image-service:8082/probe/ping?ms=200');
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(0.001);
}
