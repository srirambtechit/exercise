echo -n 'Mocking starts'
cat /mock/worker-blob-net.service | sh
cat /mock/worker-cloud-net.service | sh
echo -n 'Mocking ends'