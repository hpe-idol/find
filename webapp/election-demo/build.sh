#! /usr/bin/env bash

project_dir="$(dirname "$0")"

FIND_BASE_URL=http://localhost:8080 \
    MMAP_BASE_URL=http://demo.havendemo.com/mmap_1 \
    node "$project_dir"/backend/build-data &&
    cd "$project_dir"/frontend &&
    npm run build
