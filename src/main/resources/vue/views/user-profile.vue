<template id="user-profile">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        User Profile
      </div>
      <div class="card-body">
        <form v-if="user">
          <label class="col-form-label">User ID: </label>
          <input class="form-control" v-model="user.id" name="id" type="number" readonly/><br>
          <label class="col-form-label">User Name: </label>
          <input class="form-control" v-model="user.user_name" name="user_name" type="text"/><br>
          <label class="col-form-label">Email: </label>
          <input class="form-control" v-model="user.email" name="email" type="email"/><br>
          <div class="card-footer text-center">
            <div v-if="user">
              <a :href="`/users/${user.id}/activities`">View User Activities</a>
            </div>
          </div>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("user-profile", {
  template: "#user-profile",
  data: () => ({
    user: null
  }),
  created: function () {
    const userId = this.$javalin.pathParams["user-id"];
    const url = `/api/users/${userId}`
    axios.get(url)
        .then(res => this.user = res.data)
        .catch(() => alert("Error while fetching user" + userId));
  }
});
</script>