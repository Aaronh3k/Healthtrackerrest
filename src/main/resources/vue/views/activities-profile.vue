<template id="activities-profile">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        Activity Profile
      </div>
      <div class="card-body">
        <form v-if="activity">
          <label class="col-form-label">Activity Id: </label>
          <input class="form-control" v-model="activity.id" name="id" type="number" readonly/><br>
          <label class="col-form-label">Description: </label>
          <input class="form-control" v-model="activity.description" name="description" type="text"/><br>
          <label class="col-form-label">Duration: </label>
          <input class="form-control" v-model="activity.duration" name="duration" type="number"/><br>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("activities-profile", {
  template: "#activities-profile",
  data: () => ({
    activity: null
  }),
  created: function () {
    const activityId = this.$javalin.pathParams["activity-id"];
    const url = `/api/ui/activities/${activityId}`
    axios.get(url)
        .then(res => this.activity = res.data)
        .catch(() => alert("Error while fetching user" + acrtivityId));
  }
});
</script>