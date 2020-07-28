// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Switch the view of the user (from student to tutor or vice versa). */
function switchView() {
    fetch('/switch-view', {method: 'POST'}).then((response) => {
        // if the user who tried to switched views is not the current user
        if(response.redirected) {
            window.location.href = response.url;
            alert("You do not have permission to switch views.");
            return;
        }

        location.reload();
    });
}

